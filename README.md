# digger

Simple, multi-threaded, single-domain web crawler.

## Usage

The recommended way of running digger is using [Leiningen](https://leiningen.org/). Once in the repository's root, run:

```
$ lein run http://rbaron.net
```

This will output the result of crawling to `stdout`. An example of output is:

```
$ lein run http://rbaron.net | jq .
[
  {
    "url": "http://rbaron.net",
    "assets": [
      "http://rbaron.net//fonts.googleapis.com/css?family=Raleway:400,300,600",
      "http://rbaron.net/css/normalize.css",
      "http://rbaron.net/css/custom.css",
      "http://rbaron.net/css/skeleton.css"
    ]
  },
  {
    "url": "http://rbaron.net/blog",
    "assets": [
      "http://rbaron.net//fonts.googleapis.com/css?family=Raleway:400,300,600",
      "http://rbaron.net/css/normalize.css",
      "http://rbaron.net/css/custom.css",
      "http://rbaron.net/css/skeleton.css"
    ]
  },
  ...
]
```

You can also pack the whole program as an standalone `jar` and run it using the java run time:

```
$ lein uberjar
$ java -jar target/uberjar/digger-0.1.0-standalone.jar http://rbaron.net
```

## Debugging

Adding a `--debug` switch will cause digger to run in debug mode. It will run in a single thread and output info to stderr while it's running, so you can still pipe the output to a file or another program in your terminal. E.g.:

```
$ lein run "http://rbaron.net" --debug | jq .
Visited: 0 / URLs: 1 / Visiting http://rbaron.net
Visited: 1 / URLs: 5 / Visiting http://rbaron.net/projects
Visited: 2 / URLs: 8 / Visiting http://rbaron.net/blog/2014/01/02/Simulating-dynamic-systems-in-javascript.html
Visited: 3 / URLs: 10 / Visiting http://rbaron.net/cv
Visited: 4 / URLs: 12 / Visiting http://rbaron.net/files/cv_raphael_baron.pdf
Visited: 4 / URLs: 11 / Visiting http://rbaron.net/contact
Visited: 5 / URLs: 13 / Visiting http://rbaron.net/files/rbaron_at_rbaron.net.asc
Visited: 6 / URLs: 11 / Visiting http://rbaron.net/blog
[
  {
    "url": "http://rbaron.net",
    "assets": [
      "http://rbaron.net//fonts.googleapis.com/css?family=Raleway:400,300,600",
      "http://rbaron.net/css/normalize.css",
      "http://rbaron.net/css/custom.css",
      "http://rbaron.net/css/skeleton.css"
    ]
  },
  {
    "url": "http://rbaron.net/projects",
    "assets": [
      "http://rbaron.net//fonts.googleapis.com/css?family=Raleway:400,300,600",
      "http://rbaron.net/css/normalize.css",
      "http://rbaron.net/css/custom.css",
      "http://rbaron.net/css/skeleton.css"
    ]
  },

  ...
]
```

## Running tests

```
$ lein test
lein test digger.core-test

lein test digger.crawler-test

lein test digger.parser-test

lein test digger.utils-test

Ran 8 tests containing 31 assertions.
0 failures, 0 errors.
```

## License

MIT.
