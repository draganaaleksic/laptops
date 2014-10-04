(ns laptops.controllor
  (:use (sandbar stateful-session))
  (:require [laptops.neo4j :as n4j]
	    [laptops.view :as lv]
      [laptops.validator :refer [login-credential-errors create-user-errors 
                                               create-laptop-errors calculate-price-errors ]]
      [laptops.k-nearest-neighbors :refer [classify]]
      [cljs.reader :refer [read-string]]
      [laptops.core :refer [parse-double]])
 ; (:refer-clojure :exclude [read-string])
  )


 (defn authenticate-user
  "Authenticate user if exists in databse"
  [req-params]
  (let [email (:email req-params)
	password (:password req-params)]
    (doseq [[id
	     name
	     surname
		 email
         username
	     age
	     city
	     country
	     gender]
	    (:data (n4j/cypher-query (str "start n=node("(clojure.string/join ","(n4j/get-type-indexes "User"))")
					   where n.email = '"email"' and n.password = '"password"'				
					   return ID(n),
						  n.name,
						  n.surname,
						  n.email,
						  n.username,
              n.password,
						  n.age,
						  n.city,
						  n.country,
						  n.gender;")))]
      (println "name je " name)
       (println "surname je " surname)
	(session-put! :id id)
	(session-put! :name name)
	(session-put! :surname surname)
 (println (str "surname kontrolor" (session-get :surname)))
	(session-put! :age age)
	(session-put! :city city)
	(session-put! :country country)
	(session-put! :gender gender))
	(session-put! :login-error 1)))
 
 (defn save-laptop
  "Save newly registered user"
  [req-params]
  (println "Usao u kontrolora save laptop.")
 (if-let [user-errors (create-laptop-errors {:model (:model req-params)                          
					    :screen-size (:screen-size req-params)
              :price (:price req-params) 
					    :processor (:processor req-params)
					    :ram (:ram req-params)})]
    ;(println (str "user errors: " user-errors))
    (session-put! :error-save-laptop user-errors)
  ; (do
     (n4j/create-node "Laptop" {                             
			     :model (:model req-params)
			     :screen-size (:screen-size req-params)
			   ;  :processor (:processor req-params)
         :processor(parse-double (str (:processor req-params)))
         :price(parse-double (str (:price req-params)))
          ; :ram (:ram req-params)
          :ram (parse-double (str (:ram req-params)))
           })
   ; (session-put! :error-save-laptop nil))
    ))
 
 (defn calculate-price
  "Authenticate user if exists in databse"
  [req-params]
  (if-let [user-errors (calculate-price-errors {:processor (:processor req-params)
                                                :ram (:ram req-params)})]
  (session-put! :calculate-error user-errors)
  (let [processor (:processor req-params)
	ram (:ram req-params)
	
	    data (n4j/cypher-query (str "start n=node("(clojure.string/join ","(n4j/get-type-indexes "Laptop"))")				   				
					   return 
						  n.price,
						  n.processor,
						  n.ram;"))
     ]
       (println "sve iz baze je " (:data data))
    (println "daj boze"(into [] (map #(vector (first %) (vector (second %)(last %))) (:data data))))
    (println "sve zajedno " (vector processor ram)(into [] (map #(vector (first %) (vector (second %)(last %))) (:data data))) 3)
; (println   (classify (vector (parse-double (str processor)) (parse-double (str ram)))(into [] (map #(vector (first %) (vector (second %)(last %))) (:data data))) 3))
(let [calculated-price (classify (vector (parse-double (str processor)) (parse-double (str ram)))(into [] (map #(vector (first %) (vector (second %)(last %))) (:data data))) 3)] 
; (classify [(parse-integer (str processor)) (parse-integer (str ram))] sample-data 3)
  (session-put! :calculated-price calculated-price)
  (println "cena je kontrolor" (session-get :calculated-price))
  ) 
(println "prosaoooooo")
 ))
   
 )
 
 (defn save-user
  "Save newly registered user"
  [req-params]
  (println "Usao u kontrolora save user.")
 (if-let [user-errors (create-user-errors {:name (:name req-params)
					    :surname (:surname req-params)
					    :email (:email req-params)
					    :username (:username req-params)
					    :password (:password req-params)
					    :age (:age req-params)
					    :city (:city req-params)
					    :country (:country req-params)
					    :gender (:gender req-params)})]
    ;(println (str "user errors: " user-errors))
    (session-put! :error-register user-errors)
    (n4j/create-node "User" {
                             
			     :name (:name req-params)
			     :email (:email req-params)
			     :surname (:surname req-params)
           :username (:username req-params)
			     :password (:password req-params)
			     :age (read-string (:age req-params))
		       :city (:city req-params)
			     :country (:country req-params)
			     :gender (:gender req-params)
			     
			     
			    })
    
    ))
 
 (defn check-email
  "Checks if email exists"
  [req-params]
  (println "Usao u kontrolora check email.")
  (let [email (:email req-params)]
    (doseq [[id
	     name
	     surname
		 email
         username
	     age
	     city
	     country
	     gender]
	    (:data (n4j/cypher-query (str "start n=node("(clojure.string/join ","(n4j/get-type-indexes "User"))")
					   where n.email = '"email"'			
					   return ID(n),
						  n.name,
						  n.surname,
						  n.email,
						  n.username,
						  n.age,
						  n.city,
						  n.country,
						  n.gender;")))]
      (println "name je " name)
       (println "surname je " surname)
	(session-put! :id id)
	(session-put! :name name)
	(session-put! :surname surname)
 (println (str "surname kontrolor" (session-get :surname)))
	(session-put! :age age)
	(session-put! :city city)
	(session-put! :country country)
	(session-put! :gender gender))
	(session-put! :check-email-error 1)))
 
 
 (defn update-user
  "Update user in neo4j database"
  [req-params]
  (if-let [user-errors (create-user-errors {:name (:name req-params)
					    :surname (:surname req-params)
					    :email (:email req-params)
					    :username (:username req-params)
					    :password (:password req-params)
					    :age (:age req-params)
					    :city (:city req-params)
					    :country (:country req-params)
					    :gender (:gender req-params)})]
   ; (println (str "user errors: " user-errors))
   (session-put! :update-error user-errors)
    (let [node (n4j/read-node (session-get :id))]
      (n4j/update-node node
		       {:name (:name req-params)
			:surname (:surname req-params)
			:email (:email req-params)
			:username (:username req-params)
			:password (:password req-params)
			:age (:age req-params)
			:city (:city req-params)
			:country (:country req-params)
			:gender (:gender req-params)}))))
 
 (defn delete-user
  "Delete user from neo4j database"
  [id]
  (n4j/delete-node "User" id)
  (println "Obisan user"))
 

(defn is-logged-in
  "Checks if user is logged in"
  [response-fn]
  (if (= (session-get :id) nil)
      (lv/login)
      (do (session-pop! :login-try 1)
	  response-fn)))

(defn is-not-logged-in
  "Checks if user is logged in"
  [response-fn]
  (if (= (session-get :id) nil)
      response-fn
      (lv/home)))

