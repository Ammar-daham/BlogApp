# Single User Blogging App

This is a simple multiple-users blogging app.

All the tasks below have been completed both on the client and server sides.

`Users:`

- [x] Register new user by username and password
- [x] Login with existing username and password
- [x] Logout

`Blogs:`

- [x] User can add new blog
- [x] user can review other users blog
- [x] user can delete own blogs


## Compiling

For compiling, you'll need Maven: https://maven.apache.org/download.cgi

To compile, enter this command:

```
mvn clean package
```

## Running

To run the application, enter this command:

```
java -jar target/blogapp-1.0.0-jar-with-dependencies.jar
```

as alternative there is build.sh file which it allows you to build and run the application automatically.

## Using

To use the app, open the following address in your web browser:

    http://localhost:8080

You are initially presented with a list of current blog posts. To view a post, click the `View this post` link.

To create a new blog post, just click the `Create New Post` button. A new dialog pops up. Enter title and text for the new post and then click `Save changes`.

To delete a blog post, click the `Delete` link in the home view.

## Data storage

The blog posts are saved in a file called *posts.json* at the current directory.
The users are saved in a file called *users.json* at the current directory.