Application for checking the prices of laptops in a computer shop

Application is designed for employee in the computer shop. It allows user to enter the characteristics (model,  screen size, processor GHz rating, RAM and price) of laptops that he sold in the previous period. Main use of the application is to predict price of a laptop when user enters  processor GHz rating and RAM for  a specific laptop.
By finding a set of laptops similar to the laptop that interests the user, algorithm can average their prices
and make a guess at what the price should be for this specific laptop. This approach is called k-nearest neighbors (kNN).  Implemented kNN algorithm uses Euclidean distance for measuring how similar two laptops are. When finding the most similar laptops, algorithm orders them in ascending order and takes prices from the first three in the order (k=3). Then system finds average price for this three and displays it to the user as estimated price of the entered laptop. 
User can also list all laptops that exist in the system, he can change laptop characteristics if he wants, and he can delete laptop from the system if he for example entered wrong data about it. 
Application allows only registered users to use it, so when someone starts the application, he should first register, and then login with his email adress and password. There is an option in the application to handle the case when  user forgot his password, or if he wants to delete an account.

Starting the application

Clone project from github
Download Neo4J server from http://www.neo4j.org/ and start server 
From project root in terminal run command "lein repl" and in repl run command "(start-server)" to start the application 
At first run, app will automaticaly add one user (email:employee@shop.rs, password: employee) 

Leiningen
Version 2.0 Leiningen is a tool for building of code, written in Clojure. Leiningen is much simpler comparing with Maven and allows to define project's configuration using Clojure. Leiningen uses external tools and libraries to resolve dependencies and build a code, so it's pretty small.http://leiningen.org/

Clojure
dependency - [org.clojure/clojure "1.5.1"] Clojure is a functional general-purpose language. Its focus on programming with immutable values and explicit progression-of-time constructs are intended to facilitate the development of more robust programs, particularly multithreaded ones.http://clojure.org/

Ring
dependency - [ring/ring-jetty-adapter "1.1.0"] [ring/ring-core "1.2.0"] Ring is a library with capabilities to provide a reasonably pleasant HTTP interface. Ring is also a Clojure wrapper around the Java HTTP Servlet API.https://github.com/ring-clojure/ring

Neo4J
Neo4j is a robust (fully ACID) transactional property graph database. Due to its graph data model, Neo4j is highly agile and blazing fast. For connected data operations, Neo4j runs a thousand times faster than relational databases.
http://www.neo4j.org/

Enlive
Enlive is a selector-based (à la CSS) templating library for Clojure.
https://github.com/cgrand/enlive

Valip
dependency - [com.cemerick/valip "0.3.2"] Valip is a validation library for Clojure. It is primarily designed to validate keyword-string maps, such as one might get from a HTML form.
https://github.com/cemerick/valip

Sandbar
dependency - [sandbar "0.4.0-SNAPSHOT"] Sandbar is a web application library which is designed to be used with Compojure and/or Ring. It builds on these projects providing the following additional features: Session and flash as a global map, Authorization and authentication, including built-in support for form-based authentication, Forms and form validation
https://github.com/brentonashworth/sandbar

License
Distributed under the Eclipse Public License, the same as Clojure.
