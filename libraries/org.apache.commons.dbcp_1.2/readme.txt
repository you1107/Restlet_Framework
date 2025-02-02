--------------------
Jakarta Commons DBCP
--------------------

"Many Jakarta projects support interaction with a relational database. Creating
a new connection for each user can be time consuming (often requiring multiple 
seconds of clock time), in order to perform a database transaction that might 
take milliseconds. Opening a connection per user can be unfeasible in a 
publicly-hosted Internet application where the number of simultaneous users can 
be very large. Accordingly, developers often wish to share a "pool" of open 
connections between all of the application's current users. The number of users 
actually performing a request at any given time is usually a very small 
percentage of the total number of active users, and during request processing 
is the only time that a database connection is required. The application itself 
logs into the DBMS, and handles any user account issues internally.

There are several Database Connection Pools already available, both within 
Jakarta products and elsewhere. This Commons package provides an opportunity to 
coordinate the efforts required to create and maintain an efficient, 
feature-rich package under the ASF license."

For more information:
http://jakarta.apache.org/commons/dbcp/