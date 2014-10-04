(ns laptops.controllor
  (:use (sandbar stateful-session))
  (:require [laptops.neo4j :as n4j]
	    [laptops.view :as lv]
      [laptops.validator :as validator]
      [laptops.k-nearest-neighbors :refer [classify]]
      [cljs.reader :refer [read-string]]
      [laptops.core :refer [parse-double]])
      (:refer-clojure :exclude [read-string]))


 (defn authenticate-user
  "Authenticate user if exists in neo4j database"
  [req-params]
  (let [email (:email req-params)
	      password (:password req-params)]
    (doseq [[id
	     name
	     surname
		   email
       username]
	    (:data (n4j/cypher-query (str "start n=node("(clojure.string/join ","(n4j/get-type-indexes "Employee"))")
					   where n.email = '"email"' and n.password = '"password"'				
					   return ID(n),
						  n.name,
						  n.surname,
						  n.email,
						  n.username,
              n.password;")))]
	(session-put! :id id)
	(session-put! :name name)
	(session-put! :surname surname))
  (session-put! :login-error 1)))
 
 (defn save-laptop
  "Save new laptop in neo4j database"
  [req-params]
 (if-let [user-errors (validator/create-laptop-errors {
              :model (:model req-params)                          
					    :screen-size (:screen-size req-params)
              :price (:price req-params) 
					    :processor (:processor req-params)
					    :ram (:ram req-params)})]
    (session-put! :error-save-laptop user-errors)
     (n4j/create-node "Laptop" {                             
			        :model (:model req-params)
			        :screen-size (:screen-size req-params)			  
              :processor(parse-double (str (:processor req-params)))
              :price(parse-double (str (:price req-params)))       
              :ram (parse-double (str (:ram req-params)))})))
 
 (defn calculate-price
  "Calculate price of laptop"
  [req-params]
  (if-let [user-errors (validator/calculate-price-errors {
              :processor (:processor req-params)
              :ram (:ram req-params)})]
  (session-put! :calculate-error user-errors)
  (let [processor (:processor req-params)
	     ram (:ram req-params)
	     data (n4j/cypher-query (str "start n=node("(clojure.string/join ","(n4j/get-type-indexes "Laptop"))")				   				
					    return 
						  n.price,
						  n.processor,
						  n.ram;"))]
  (let [calculated-price (classify (vector (parse-double (str processor)) (parse-double (str ram)))(into [] (map #(vector (first %) (vector (second %)(last %))) (:data data))) 3)] 
  (session-put! :calculated-price calculated-price)))))
 
 (defn save-user
  "Save newly registered user in neo4j database"
  [req-params] 
 (if-let [user-errors (validator/create-user-errors {
             :name (:name req-params)
					    :surname (:surname req-params)
					    :email (:email req-params)
					    :username (:username req-params)
					    :password (:password req-params)})]   
    (session-put! :error-register user-errors)
    (n4j/create-node "Employee" {                             
			        :name (:name req-params)
			        :email (:email req-params)
			        :surname (:surname req-params)
              :username (:username req-params)
			        :password (:password req-params)})))
 
 (defn check-email
  "Checks if email exists in neo4j database"
  [req-params]
  (println "Usao u kontrolora check email.")
  (let [email (:email req-params)]
    (doseq [[id
	     name
	     surname
		   email
       username]
	    (:data (n4j/cypher-query (str "start n=node("(clojure.string/join ","(n4j/get-type-indexes "Employee"))")
					   where n.email = '"email"'			
					   return ID(n),
						  n.name,
						  n.surname,
						  n.email,
						  n.username;")))]
	(session-put! :id id)
	(session-put! :name name)
	(session-put! :surname surname))
	(session-put! :check-email-error 1)))
 
 
 (defn update-user
  "Update user in neo4j database"
  [req-params]
  (if-let [user-errors (validator/create-user-errors {
              :name (:name req-params)
					    :surname (:surname req-params)
					    :email (:email req-params)
					    :username (:username req-params)
					    :password (:password req-params)})]
   (session-put! :update-error user-errors)
    (let [node (n4j/read-node (session-get :id))]
      (n4j/update-node node
		       {:name (:name req-params)
			      :surname (:surname req-params)
			      :email (:email req-params)
			      :username (:username req-params)
			      :password (:password req-params)}))))
 
 (defstruct laptop :id :model :price :processor :ram)
 
 (defn all-laptops
  "Select all laptops for neo4j database"
  []
  (let [data (n4j/cypher-query (str "start n=node("(clojure.string/join ","(n4j/get-type-indexes "Laptop"))")				   				
					   return ID(n),
						  n.model,					 
						  n.price,
              n.processor,
						  n.ram;"))]
  (let [laptops (vec (map #(apply struct laptop %) (partition 5 (apply concat (:data data)))))]
  (session-put! :all-laptops laptops))))
 
(defn delete-user
  "Delete user from neo4j database"
  [id]
  (n4j/delete-node "Employee" id))
 
(defn delete-laptop
  "Delete laptop from neo4j database"
  [id]
  (n4j/delete-node "Laptop" id))
 
 (defn update-laptop
  "Update laptop in neo4j database"
  [req-params]
  (if-let [user-errors (validator/create-laptop-errors {
              :model (:model req-params)                          
					    :screen-size (:screen-size req-params)
              :price (:price req-params) 
					    :processor (:processor req-params)
					    :ram (:ram req-params)})]
   (session-put! :update-error user-errors)
    (let [node (n4j/read-node (read-string (str (:id req-params))))]
      (n4j/update-node node
		       {  :model (:model req-params)
			        :screen-size (:screen-size req-params)			  
              :processor(parse-double (str (:processor req-params)))
              :price(parse-double (str (:price req-params)))       
              :ram (parse-double (str (:ram req-params)))}))))
 
 (defn default-nodes-in-database
   "Insert one Employee and two Laptops 
   soo app won't crush at the beginnig
   when there is no nodes in database"
   []
     (n4j/create-node "Employee" {                             
			        :name "Dragana"
			        :email "employee@shop.rs"
			        :surname "Aleksic"
              :username "employee1"
			        :password "employee"})
     (n4j/create-node "Laptop" {                             
			        :model "TOSHIBA"
			        :screen-size "12"			  
              :processor(parse-double "2.3")
              :price(parse-double "500")       
              :ram (parse-double "4")})
     (n4j/create-node "Laptop" {                             
			        :model "Lenovo"
			        :screen-size "12"			  
              :processor(parse-double "1.2")
              :price(parse-double "230")       
              :ram (parse-double "3")}))

(defn is-logged-in
  "Checks if user is logged in"
  [response-fn]
  (if (= (session-get :id) nil)
      (lv/login)     
	    response-fn))

(defn is-not-logged-in
  "Checks if user is logged in"
  [response-fn]
  (if (= (session-get :id) nil)
      response-fn
      (lv/home)))

