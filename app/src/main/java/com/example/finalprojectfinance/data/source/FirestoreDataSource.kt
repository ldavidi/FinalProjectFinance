package com.example.finalprojectfinance.data.source

import com.example.finalprojectfinance.data.model.Account
import com.example.finalprojectfinance.data.model.Transaction
import com.example.finalprojectfinance.data.model.Holding
import com.example.finalprojectfinance.data.model.Loan
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot                // ← import this
import com.google.firebase.firestore.FirebaseFirestoreException   // ← and this
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class FirestoreDataSource {

    private val db = FirebaseFirestore.getInstance()
    private val accountsCol = db.collection("accounts")
    private val txCol = db.collection("transactions")
    private val holdingsCol = db.collection("holdings")
    private val loansCol = db.collection("loans")

    /** Stream *all* transactions across every account */
    fun getAllTransactions(): Flow<List<Transaction>> = callbackFlow {
        val sub = txCol
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }
                val list = snap
                    ?.documents
                    ?.mapNotNull { it.toObject(Transaction::class.java) }
                    ?: emptyList()
                trySend(list).isSuccess
            }
        awaitClose { sub.remove() }
    }
    /** Stream all accounts as a Flow, updating on any change */
    fun getAllAccounts(): Flow<List<Account>> = callbackFlow {
        val sub = accountsCol.addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }
            val list = snap?.documents
                ?.mapNotNull { it.toObject(Account::class.java)?.copy(id = it.id) }
                ?: emptyList()
            trySend(list).isSuccess
        }
        awaitClose { sub.remove() }
    }

    /** Create or update an account, and ensure 'id' matches the Firestore document ID */
    suspend fun upsertAccount(account: Account) {
        if (account.id.isBlank()) {
            // 1) reserve a new doc ID
            val docRef = accountsCol.document()
            // 2) write the account with the id field populated
            docRef
                .set(account.copy(id = docRef.id))
                .await()
        } else {
            // updating an existing account: just overwrite with the correct id
            accountsCol
                .document(account.id)
                .set(account)
                .await()
        }
    }


    /** Delete an account and its transactions */
    suspend fun deleteAccount(accountId: String) {
        accountsCol.document(accountId).delete().await()
        // cascade‐delete related transactions
        val txDocs = txCol.whereEqualTo("accountId", accountId).get().await()
        txDocs.documents.forEach { it.reference.delete() }
    }

    /** Stream all transactions for an account */
    fun getTransactionsFor(accountId: String): Flow<List<Transaction>> = callbackFlow {
        val sub = txCol
            .whereEqualTo("accountId", accountId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }
                val list = snap
                    ?.documents
                    ?.map { doc ->
                        // pull each field explicitly
                        val id = doc.id
                        val acctId = doc.getString("accountId") ?: ""
                        val amount = doc.getDouble("amount") ?: 0.0
                        // Firestore might store timestamp as a Timestamp _or_ a Long,
                        // so check both:
                        val tsLong = doc.getLong("timestamp")
                        val tsStamp = doc.getTimestamp("timestamp")
                        val timestamp = tsLong
                            ?: tsStamp?.toDate()?.time
                            ?: 0L
                        val type = doc.getString("type") ?: "DEPOSIT"

                        Transaction(
                            id = id,
                            accountId = acctId,
                            amount = amount,
                            timestamp = timestamp,
                            type = type
                        )
                    }
                    ?: emptyList()
                trySend(list).isSuccess
            }
        awaitClose { sub.remove() }
    }


    /** Add a transaction and update the corresponding account’s balance */
    suspend fun addTransaction(tx: Transaction) {
        // 1) add the transaction
        txCol.add(tx).await()
        // 2) update balance atomically
        db.runTransaction { trans ->
            val accRef = accountsCol.document(tx.accountId)
            val accSnap = trans.get(accRef)
            val oldBal = accSnap.getDouble("balance") ?: 0.0
            val newBal = if (tx.type == "DEPOSIT") oldBal + tx.amount else oldBal - tx.amount
            trans.update(accRef, "balance", newBal)
        }.await()
    }

    fun getAllHoldings(): Flow<List<Holding>> = callbackFlow {
        val sub = holdingsCol.addSnapshotListener { snap, err ->
            if (err != null) {
                close(err); return@addSnapshotListener
            }
            val list = snap?.documents
                ?.map { doc ->
                    Holding(
                        id = doc.id,
                        ticker = doc.getString("ticker") ?: "",
                        shares = doc.getDouble("shares") ?: 0.0,
                        currentPrice = doc.getDouble("currentPrice") ?: 0.0
                    )
                } ?: emptyList()
            trySend(list).isSuccess
        }
        awaitClose { sub.remove() }
    }

    // 2. Create or update a holding
    suspend fun upsertHolding(h: Holding) {
        if (h.id.isBlank()) {
            val docRef = holdingsCol.document()
            docRef.set(h.copy(id = docRef.id)).await()
        } else {
            holdingsCol.document(h.id).set(h).await()
        }
    }

    suspend fun deleteHolding(id: String) {
        holdingsCol.document(id).delete().await()
    }

    /** Stream all loans in real time */
    fun getAllLoans(): Flow<List<Loan>> = callbackFlow {
        val sub = loansCol.addSnapshotListener { snap: QuerySnapshot?,
                                                 err: FirebaseFirestoreException? ->           // ← specify types!

            if (err != null) {
                close(err)
                return@addSnapshotListener
            }
            val list = snap
                ?.documents
                ?.mapNotNull { it.toObject(Loan::class.java)?.copy(id = it.id) }
                ?: emptyList()
            trySend(list).isSuccess
        }
        awaitClose { sub.remove() }
    }

    /** Create or update a loan */
    suspend fun upsertLoan(loan: Loan) {
        if (loan.id.isBlank()) {
            val ref = loansCol.document()
            ref.set(loan.copy(id = ref.id)).await()
        } else {
            loansCol.document(loan.id).set(loan).await()
        }
    }

    /** Delete a loan */
    suspend fun deleteLoan(id: String) {
        loansCol.document(id).delete().await()
    }
}