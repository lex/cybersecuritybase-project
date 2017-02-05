# A1 (SQL) Injection

#### Steps to Reproduce
1. Create some accounts on the front page
2. Go back to the front page and type "' or 1=1--" in the name search without the quotes
3. Click Search
4. Every user should be listed on the next page.

#### How to fix
[The line 62 in SignupController.java](src/main/java/sec/project/controller/SignupController.java#L62) executes a SQL query which is constructed directly from the request:

```
        Query q = e.createNativeQuery("select * from signup s where s.name like '" + name + "'", Signup.class);
```
It can be fixed by setting the parameters correctly:

```
        Query q = e.createNativeQuery("select * from signup s where s.name like ?", Signup.class);
        q.setParameter(1, name);
```

# A3 Cross-Site Scripting

#### Steps to Reproduce
1. Create an account with name ```<marquee>Bob</marquee>``` and some address 
2. Bob should be scrolling sideways on the next page if the browser supports it

#### How to fix
[On line 12 of done.html](src/main/resources/templates/done.html#L12) the name is shown as unescaped text:

```
        <p th:utext="${signup.getName()}">name</p>
```

It can be fixed by showing escaped text instead:
```
        <p th:text="${signup.getName()}">name</p>
```

# A4 Insecure Direct Object References

#### Steps to Reproduce:
1. Create some accounts on the front page
2. After creating some accounts, click on "You can check your information here at any time." at the bottom of the confirmation page
3. Modify the parameter *id* in the url bar (*/details?id=2* -> */details?id=1*)
4. The information from some other signup is now viewable

#### How to fix:

The problem is that the signup details are fetched by their id which is linear and incrementing by its nature. In a real case there should be user accounts and the information should come from the account directly.

In this case [GUIDs](https://en.wikipedia.org/wiki/Universally_unique_identifier) could be used to make the ids much more harder to guess but they would still be accessible by anyone.

# A8 CSRF

#### Steps to Reproduce
1. Create an account on the front page
2. Open [csrf.html](csrf.html) supplied with the repository
3. Click the button "click to hack"
4. The user account should now be deleted

#### How to fix
[On line 28 of SecurityConfiguration.java](src/main/java/sec/project/config/SecurityConfiguration.java#L28) there's a line which disables CSRF tokens:

```
        // disable csrf protection so it doesn't get in the way
        http.csrf().disable();
```

If removed, POST requests will require a CSRF token for validating them.

# A10 Unvalidated redirects

#### Steps to Reproduce
1. Create an account on the front page
2. Open [http://127.0.0.1:8080/verify?id=2&redirect=https://google.fi](http://127.0.0.1:8080/verify?id=1&redirect=https://google.fi)
3. The user account should now be verified and the user redirected to Google instead of the intended page 

#### How to fix
[On line 75 of SignupController.java:](src/main/java/sec/project/controller/SignupController.java#L75)

```
        return "redirect:" + redirect;
```

*redirect* is taken directly from the request's URL. To avoid malicious intents the redirection paths should be hardcoded somewhere safe.
