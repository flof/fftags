fftags
======

A JSP Taglib for stateless web applications.

Installation
------------

- Clone the fftags repository
- Run `mvn install` in the root directory of fftags to install fftags in your local maven repo
- Include the following dependency in the *pom.xml* of your project:

```
    <dependency>
        <groupId>net.sourcecoder</groupId>
        <artifactId>fftags</artifactId>
        <version>1.1.0.RELEASE</version>
    </dependency>
```

Usage
-----

In web applications using the RESTful architectural style, one key constraint is statelessness. That means that the current state of the application is not stored on the server for each user (in the user-session) but is completely contained in the current URL of the web browser of the user.

But what is the application state?

Let's use an example. Consider a knowledge base application for storing and retrieving knowledge base entries. Each entry consists of:

 - an ID
 - a subject 
 - a category
 - a creation date
 - a last-modified-date

We are focusing on the retrieval part of the application. Users can enter a  search term into a textfield to search for entries. The result is listed in a table with a column for each field. In each column header are two links for sorting the result by this column in ascending or descending order. Then there are navigation links where the user can go to the next or previous page. In this example, the application state contains the following information:

 - The search term
 - The current page number
 - The sort column
 - The sort direction (ascending, descending)

So this information must be contained in the URL of the webapplication. Let's define URL query parameters:

- Search term: Parameter "q"
- Current page number: Parameter "page"
- Sort column: Parameter "sort_col"
- Sort direction: Parameter "sort_dir"

The URL might look like this:

`http://example.com/kb?q=mysearchterm&page=1&sort_col=creation_date&sort_dir=desc`

When this URL is opened in a web browser, the server receives all the information necessary to render the resulting page. The server can use the Parameter "q" in the SQL-Statement to filter the result by the search term. The server can also use the Parameters "sort_col" and "sort_dir" in the SQL-Statement to sort the result accordingly. And finally the server can extract the right number of entries from the search result by using the Parameter "page". The server does not depend on any prior knowledge associated with the current users session. All the information necessary is provided by the URL.

But why? What's the advantage?

- Better scalability since the server doesn't has to store a lot of information in each users session.
- Bookmarkable URLs for the users. Whereever the user currently is in the web application, he or she can always add a bookmark in the browser to access the exact same location at a later point in time.
- The browsers back- und forward buttons work as expected by the user.

So what's **fftags** for?

When you make a web application in the style described above, you have to add the whole application state in each in every Link (and form action) you have on your pages (with some exceptions). And that can be a lot.