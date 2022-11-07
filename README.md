# Single User Blogging App

This is a simple single-user blogging app.

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

## Using

To use the app, open the following address in your web browser:

    http://localhost:8080
    
You are initially presented with a list of current blog posts. To view a post, click the `View this post` link.
    
To create a new blog post, just click the `Create New Post` button. A new dialog pops up. Enter title and text for the new post and then click `Save changes`.

To delete a blog post, click the `Delete` link in the home view.

## Data storage

The blog posts are saved in a file called *posts.json* at the current directory.