# Android-Permission-Usage-Without-Defining
Android Signature Level Permissions to Use Undefined Permissions

In this project there are two applications.
First one, named as "first", uses the permission to read contacts and creates a background service to provide the contact list if it is asked. 
The service is proteced with a Signature Level permission

The second app, named as "second", is only uses the permission to protect the first's background service. 
Just because they are signed with the same developer certificate, none of the permissions are requested upon the installation of the second app.

Second app triggers the first app's background service with an intent and gets the contact db without using the <b>com.android.READ_CONTACTS</b> permission and sends all the contact list to the C&C server as a parameter of GET request by triggering web browser app.

At the end second app uses the "read contact" priviledge of first app and the "internet" priviledge of the Browser app.

Blog post about the scenario above
[TURKISH] http://www.oguzhantopgul.com/2014/05/androidde-izin-almadan-izin-kullanma.html
