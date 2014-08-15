<p>Account-id (the account id ex “gwJSfNRWbYKSSF4Z6GWfv5wMsG72hQ2D
zZ”)</p>
<pre><code>:transactions (contains a vector of maps for each transaction ever made in the given account. This is the same as the “tx” map in the “transactions” vector that is retrieved from the “account-tx” API method by way of :result. Each transaction map contains the following info:)
     :TransactionType (e.g. “Payment” “AccountSet”)
     :date (in seconds since Jan 1 2000)
     :Amount (amount of STR that was transaction in mm)
     :Account (the account that sent the STR)
     :Fee (the transaction fee in STR)
     :Destination (the receiver of the STR in the transaction)
     :inLedger (the ledger index of this transaction)
     :ledger_index (not sure how this is different from above)
     :LastLedgerSequence
     :TxnSignature
     :hash
     :Flags
     :SigningPubKey
     :Sequence
:statistics (contains a map of descriptive statistics about the transactions in the given account)
    :inflows (contains a map of inflow statistics)
      :total-coins (the gross number of STR that has come into    this account)
      :total-txns (the total number of inflow transactions)
      :avg-txn (the average amount each inflow transaction was)
      :txn-frqcy (contains a map with keys equal to inflow transactionamounts and values equal to the number of times an inflow transaction on the ‘key’ amount occurred in the account)
    :outflows (contains a map with the same keys as inflows but for outflow data)
    :max-bal (the highest account balance this account has had)
    :min-bal (the lowest account balance this account has had)
    :max-time (the date/time corresponding to when the account was at max balance)
    :min-time (the date/time corresponding to when the account was at min balance)
    :avg-balance (the average balance calculated as the sum of the balances each time the account had a transaction divided by the total number of transactions)
    :current-balance (the current balance of the account)
    :time-dif-max-min (the max-time – the min-time)
    :balance-over-time (contains a vector of vectors where for each subvector the first value is the date/time and the 2nd value is the balance of the account at the given date)/time
:demographics (contains a map of demographic and other data about the  account – can be populated once integrated to facebook api)
    :age
    :location
    :sex
    :ethnicity
    :education
    :other-data (other data not currently populated)
        :email-address
        :facebook-id
        :trust-lines (contains a vector of maps with the following keys)
            :trust-acct
            :trust-amt
            :trust-ccy
</code></pre>
