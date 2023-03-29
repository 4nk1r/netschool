## What's this?
This is an unofficial Android app for NetSchool electronic school journal created from scratch. The app was created as a school project, so just for educational purposes, and of course it has nothing to do with "ИрТех" and anyone else participating in creating NetSchool.

## What's under the hood?
The sources are pure Kotlin. 
- For UI, Jetpack Compose was used.
- For networking, Retrofit 2 was used.
- For DI, Dagger Hilt was used.
- For JSON parsing, GSON was used.
- For HTML parsing, Jsoup was used.

None of the libraries listed above are created by me, and they only belong to their creators.

Also, I do not pretend that the code in the repo is great. I cannot say I've definitely chosen the right ways to implement things, so keep it in mind.

## How do I build this?
This is just a common Android project, except for a thing: since many schools have their own copies of original website and due to my desire to keep my school in secret, I've extracted my school name and its journal website URL into a file excluded from the repository, so after you clone it you'll need to create your own one. The file is called `private_const.properties` and it's placed right in the root of the project. In the file, there are 2 constants: `SCHOOL_NAME` and `NETSCHOOL_BASE_URL`. It's pretty obvious what you should put in the values of those. Here's an example of the file: 
```properties
NETSCHOOL_BASE_URL="http://example.com/"
SCHOOL_NAME="МБОУ СОШ № 1"
```
Though the code above *should* work, I'd also recommend replacing cyrillic letters with unicode escape just in case. Gradle is unpredictable sometimes, you know...

That's it, you're now ready to build the project!

## Still have questions?
Feel free to contact me on Telegram: [@fournkoner](https://fournkoner.t.me/)
