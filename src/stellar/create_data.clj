(ns stellar.create_data
  (:require [stellar.stellar-api :as api])
  (:require [stellar.core :as core]))

(def account-map (atom {}))
(def acct-data-headers
  {:transactions []
   :demographic-data
   :statistics})

(swap! account-map assoc :somestring {})

(swap! account-map assoc :somestring2 {})

(swap! account-map assoc :somestring2 {})


;(zipmap (get-ledger-destinations 280000) [1 1 1])


(reset! account-map (core/get-all-acct-dest-in-range 200000 200020))
(swap! account-map merge (core/get-all-acct-dest-in-range 56 60))


(def transtemp ["there she is"])
(def temp-map @account-map)
(swap! account-map assoc-in ["gsYZFU6ztRANNPgYj3mUS28YY2GAMuZ8VN" :transactions] transtemp)
(assoc-in (temp-map "gsYZFU6ztRANNPgYj3mUS28YY2GAMuZ8VN") [:transactions] transtemp)
;(assoc-in temp-map )
(mapv key @account-map)
@account-map



