Hello and thank you for your interest in testing my application. Here are instructions on how to do so:

1.)First you will create the database for this app to use by running the "PersonRegistry.sql" script I created, located at "PersonRegistry/PersonRegistry/sql"

2.)Next you will need to provide the application your credentials with which it may connect to your newly created database. In PersonRegistry/src/main/resources/application.properties, you will add your database's url to the line "spring.datasource.url", your username at the end of the line "spring.datasource.username", and your password at the end of the line "spring.datasource.password."

3.) Then move to config.properties in the same folder (full path: PersonRegistry/src/main/resources/config.properties), you will add your database's url to the line "MYSQL_DB_URL", your username at the end of the line "MYSQL_DB_USERNAME", and your password at the end of the line "MYSQL_DB_PASSWORD."

Your application.properties file should look like this:
spring.datasource.url=<your db url here>
spring.datasource.username=<your username here>
spring.datasource.password=<your password here>

and your config.properties should look like this:

MYSQL_DB_URL=<your db url here>
MYSQL_DB_USERNAME=<your username here>
MYSQL_DB_PASSWORD=<your password here>

4.)Once you've done that, move to PersonRegistry/src/main/java/springboot/main/Application and run Application.main, then run PersonRegistry/src/main/java/controllers/AppMain using maven's javafx:run plugin. In Intellij, in the maven tab, you may double click "javafx:run" under the javafx plugin.

5.)Your application should be running. At the login page, you will see the fields are auto filled with the valid user credentials "ragnar" and "flapjacks." This is to make testing faster. You may use the first, previous, next and last buttons to read through the list of registered people, and the search bar at the top to filter people by last name. To remove the filter and list all people, hit search with no text in the search bar.

6.)To delete a person, single click their name then click delete. To add a person, click add, then fill out their fields. The valid birthday format is mm/dd/yyyy. To inspect a person, double click their name. This takes you to the person detail page. It shows your the information about the person, and if you click the audit trail tab, you can see the history of changes that person record has been through. If you change any of the fields in the detail tab, and hit save, it will update the record. If you run the application twice simultaneously and try to update the same record from both, you will see optimistic locking enforced and you will not be able to. Click cancel to leave without saving changes. 