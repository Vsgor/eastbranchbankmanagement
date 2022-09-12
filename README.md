# East Branch Bank System

My main studying pet-project. This service allows client to control one's bank account,
and transfer money to different users with rest api.

Data stores in Postgresql database with access via Hibernate ORM

Security layer is using json web token for authentication.

Service also represents aspect that logs method entry with annotation @LogMethod

This service is using kafka events to comunicate with Report Builder service:
https://github.com/Vsgor/ReportBuilder