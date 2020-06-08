# PomoTimer++

One Paragraph of project description goes here

## DEV Info
TEMP


Output location - $ shadow-cljs release app --config-merge '{:output-dir "somewhere/else"}'

7.6.1. Release Versions
shadow-cljs release app --config-merge '{:release-version "v1"}'

Service worker?
worker.js

Pushing out old cache javascript issue
7.6.2. Filenames with Fingerprint-Hash

Output Manifest? Can it be useful?

Calva cider-nrepl setup?
https://shadow-cljs.github.io/docs/UsersGuide.html#_dependencies_2

## ToDO More

Theme? (Clean / Neon)
Pulling out Main body container?
full screen timer?

Possible to refactor
    Timer output part only? (UI part only)
    Function shared between timer

Inital Entering Hi Nice page with item in center only?
Navbar hide with ----\/----- for full app-like ui?

Reframe to keep theme, or some other info to be shared between switching of pages?


### Improve build

Shadow-CLJS - is new and changing quickly. low usage langauge + low new tool = low info on web.

I had long hours following CLJS doc but some parts don't seem to work even following their standard info
Searching google other people put things in different place

Below seems to have pure cljs (not lein)
Try running and seeing what's different sometime. (Check how old since Shadow-CLJS might have changed)
https://github.com/quangv/shadow-re-frame-simple-example 
https://github.com/loganpowell/shadow-proto-starter 
https://github.com/iku000888/shadow-cljs-kitchen-async-puppeteer 
https://github.com/baskeboler/cljs-karaoke-client 

## Animation research 

### React

React-Spring chosen. non-hook version is still supported

https://www.react-spring.io - "Both render-props and hooks will be kept and maintained for the forseeable future."
https://www.framer.com/api/ - Uses hook so not compatible with re-agent
https://github.com/nearform/react-animation - 3rd option

### JS

By popularity

https://github.com/juliangarnier/anime
https://github.com/mojs/mojs
https://maxwellito.github.io/vivus/
https://github.com/IanLunn/Hover
https://github.com/thednp/kute.js/


## Requirement

Should be simple UI like google.
And have More styled version
Should be APP like, to PWA on phone.

Lets make this simple as possible. E.g Simple-style/Advanced-style over 10 different options.
That way logic to switch in nav are way simpler

## Useful

### Tech docs

stylefy - https://github.com/Jarzka/stylefy
(Alternative - https://github.com/dvingo/cljs-styled-components)

Shadow CLJS - https://shadow-cljs.github.io/docs/UsersGuide.html
Reagent - https://cljdoc.org/d/reagent/reagent/1.0.0-alpha2/doc/tutorials/react-features
concurrently - https://www.npmjs.com/package/concurrently


concurrently npm:watch-js = npm run watch-js


### Decision

CSS Grid layout, html etc info
#### HTML5 semantic elements
https://developer.mozilla.org/en-US/docs/Learn/HTML/Introduction_to_HTML/Document_and_website_structure#Enter_HTML5_structural_elements

https://clzd.me/html5-section-aside-header-nav-footer-elements-not-as-obvious-as-they-sound/

https://www.w3schools.com/html/html5_semantic_elements.asp

https://stackoverflow.com/questions/4781077/html5-best-practices-section-header-aside-article-elements


### ES6 Import => CLJS Require

See https://shadow-cljs.github.io/docs/UsersGuide.html#_using_npm_packages
For full table of examples.


### Useful commands

`shadow-cljs watch app`


### Links

Following HTML5 Standard strictly - https://html.spec.whatwg.org/multipage/ https://www.html5tutorial.info 


## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.


### Prerequisites

What things you need to install the software and how to install them

```
Give examples
```

### Installing

A step by step series of examples that tell you how to get a development env running

Say what the step will be

```
Give the example
```

And repeat

```
until finished
```

End with an example of getting some data out of the system or using it for a little demo

## Running the tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc
