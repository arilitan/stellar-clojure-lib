Stellar Clojure Library
=======================
This readme file will run through instructions on how to install this library and run some of its functions

Installation
------------
Leiningen is a great tool for automating Clojure projects. You can follow the instructions to [install Leiningen here][lein link] or if you have homebrew installed you can just type `$ brew install leiningen` into your terminal.

1. In the terminal go to the directory you wish to install the Stellar Clojure Library
2. Enter `$ git clone https://github.com/arilitan/stellar-clojure-lib.git`
3. You should now have everything you need installed

Running `lein repl` to interact with the library functions
--------------
You can use a tool built in leiningen called REPL to interact with the library functions. REPL stands for "Read-Eval-Print Loop" and its useful for experimenting with clojure code. It presents you with a prompt you can type clojure code into. It then *reads* your input, *evaluates* its, *prints* the result, and *loops* presenting you with a prompt again.

To get started, in your terminal `cd` into the `stellar-clojure-lib` folder you created.

Enter `$ lein repl`. you should see output that looks like this:
```
nREPL server started on port 60697 on host 127.0.0.1 - nrepl://127.0.0.1:6069
REPL-y 0.3.2, nREPL 0.2.3
Clojure 1.5.1
Java HotSpot(TM) 64-Bit Server VM 1.6.0_65-b14-462-11M4609
    Docs: (doc function-name-here)
          (find-doc "part-of-name-here")
  Source: (source function-name-here)
 Javadoc: (javadoc java-object-or-class-here)
    Exit: Control+D or (exit) or (quit)
 Results: Stored in vars *1, *2, *3, an exception in *e

stellar.create_data=>
```

Test some functions
--------
You should now be able to run any of the functions stellar-clojure-lib in the terminal. See below for a few demo examples. For more in depth documentation for how to use these functions and input formats [check the wiki page here][wiki].


Check ledger data
-------
Get the current ledger index number: 
```
stellar.create_data=> current-ledger-index
```
Get the transactions array for the current ledger or any ledger index for that matter:
```
stellar.create_data=> (get-ledger-txns current-ledger-index)
stellar.create_data=> (get-ledger-txns 397800)
```
See all of the account destinations in a given ledger range (5 most recent ledgers in the example)
```
stellar.create_data=> (keys (get-all-acct-dest-in-range (- current-ledger-index 5) current-ledger-index))
```

Check account data
------------------
Define an account to analyze (my account in the example but will work for any account):
```
stellar.create_data=> (def temp-acct "gwJSfNRWbYKSSF4Z6GWfv5wMsG72hQ2DzZ")
```
Get all of the :tx transaction data for the account:
```
stellar.create_data=> (get-all-txn-data-vec temp-acct)
```
Get the transaction statistics for a given account. First lets define a statistics map to analyze:
```
stellar.create_data=> (def flow-data (get-flow-data-map temp-acct))
```
Check out the map contents:
```
stellar.create_data=> flow-data
```
Display the statistics to read in a nice format:
```
stellar.create_data=> (display-data-map-stats flow-data)
```
Display the inflow frequencies by transaction amount (substitute "inflow" for "outflow" to display outflows):
```
stellar.create_data=> (display-inflow-freq flow-data)
```
Display the time series of the account's balance over time
```
stellar.create_data=> (display-balance-over-time flow-data)
```
Populate data
----------------
Running the function below will create a map with all unique account destinations in the range as keys, and the values being a map with the [schema found here][schema]. The function will also populate statistics and transactions data for all accounts in the range. This can be iterated through all ledgers in order to populate the transaction statistics for all accounts in the Stellar network. The data can then be queried to find accounts with certain specified attributes. 

Populate the statistics data for the ledger 397828 and save it to a defined map called all-data-map:
```
stellar.create_data=> (def all-data-map (get-data-in-ledgers 397828 397828))
```
Display all the data in the ledger:
```
stellar.create_data=> (all-data-map)
```


[lein link]:https://github.com/technomancy/leiningen
[wiki]:https://github.com/arilitan/stellar-clojure-lib/wiki
[schema]:https://github.com/arilitan/stellar-clojure-lib/blob/master/dataSchema.md
