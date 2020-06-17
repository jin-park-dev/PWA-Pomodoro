# PomoTimer++

One Paragraph of project description goes here

## DEV Info
TEMP


Output location - $ shadow-cljs release app --config-merge '{:output-dir "somewhere/else"}'

7.6.1. Release Versions
shadow-cljs release app --config-merge '{:release-version "v1"}'

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

stylefy - https://github.com/Jarzka/stylefy (https://jarzka.github.io/stylefy/doc/stylefy.core.html)
(Alternative - https://github.com/dvingo/cljs-styled-components)

Shadow CLJS - https://shadow-cljs.github.io/docs/UsersGuide.html

Reagent - https://cljdoc.org/d/reagent/reagent/1.0.0-alpha2/doc/tutorials/react-features (https://reagent-project.github.io/docs/master/reagent.core.html#var-create-class)

concurrently - https://www.npmjs.com/package/concurrently

animate css - https://animate.style/

concurrently npm:watch-js = npm run watch-js

calva evaulate shortcuts - https://calva.io/commands-top10/


### Decision

Following strictly guidelines of HTML5 semantic
Strictly following best practice for HTML5 semantic

CSS Grid layout, html etc info
#### HTML5 semantic elements
https://developer.mozilla.org/en-US/docs/Learn/HTML/Introduction_to_HTML/Document_and_website_structure#Enter_HTML5_structural_elements

https://clzd.me/html5-section-aside-header-nav-footer-elements-not-as-obvious-as-they-sound/

https://www.w3schools.com/html/html5_semantic_elements.asp

https://stackoverflow.com/questions/4781077/html5-best-practices-section-header-aside-article-elements


#### Service worker
Google Workbox v5.

#### Sources

| What? | Location | Type	| Comment	|
|---	|---	|---	|---	|
| Reagent SPA App | src/ | cljs | Main SPA App based on re-agent, re-frame (React)|
| Static files | public/ | Html, CSS (1 essential, 1 tailwind generated), JS (watch) | Contains service worker, html, css. Used for development. public/js and public/css/tailwind.css are generated during development. (Further improvement can be made by completely seperating out dev. ,production, files. However extra effort to seperate, auto-copy etc seems like too much effort for small project like this.) Clean will clean only generated files. |
| Build script | p-build.sh | Bash script | Run this as part of deployment |
| Publish script | publish-master.sh | Bash script | Copies SPA App to server |
| Shadow-CLJS | shadow-cljs.edn | Config for shadow-cljs | Build tool |
| TailwindCSS (PostCSS) | tailwind.config.js, postcss.config.js | js config | TailwindCSS build witt PostCSS |
| Static output | build/ | bash script | Copy Paste this to deploy. |
|========|========|========|========================================|
| dev - Githook | dev/ | bash script | For CI/CD system (jenkins) |
| Server Deployment | deployment/ | Nginx Config | Copy of nginx config for deployment |

#### Designs

There's difference is datatype passed down between pomodoro and time component. This is due to choice in date time library and lack of support for working with time duration.

This is required in the first place due to 1ms from setInterval is JS will be out of sync. On MacOS in KanXdoro, after while 25min can be up to 36min on MacBook Pro 2018.

Doing research new hip things is to go functional, immutable style of date time library in JS world. Even moments.js creator is creating new library currently not as featured but seperate to moments.

"date-fns - modern JavaScript date utility library" - is what come up in google search, highly regarded in my research.

Many library, other ones even in clojurescript does not seem to make it easy to work with duration and would require same type of calculation I have done. Turns out moments.js is very good if you need duration.

(In python world there is arrow and pendulum. Duration hasn't been issue from memory)

#### Pomodoro - Custom Data structure (as duration is not supported)

After trying this out, using moments would been much much simpler. I have to create duration as library itself does not support it.

Uses difference of time to create "duration". (Moments.js has this but Date-fns does not.) For current pomodoro, and next pomodoro, next date.

This forces lot more calculations required and holding of state to make duration.

Component, has to have custom non-datetime object passed down as hash-map with all the values.

Also I have to manually format dates coming out from duration (difference). This created way more work then going down momoents.js route.

#### Time - date-fns leveraged (as it is possible)  

As no calculation is required I can use fully use formatting from date-fns library.

#### Theme

Use tailwindcss and add in db.cljs.
Tailwind generates all the css. System is hooked up to use theme from "theme-colors" in db.cljs. Rest of system will react to "theme-colors.

##### Flow

tailwind.config.jg => tailwind_config.css => db.cljs
Add colour, Add "theme-xxxxxxx", add in theme-colors venctor.

#### Deployment

##### TailwindCSS

TailwindCSS - Purge CSS to drop size. However not worth the effort. (Gzip 144.6kb, Brotli 37kb - cloudflare)
Purge CSS work with html/JSX. Due to going outside of non-common usage I'd have to generate HTML for all the pages and get purge CSS to do the work. Too much work for almost non-noticeable change for modern western world. There could be data-limited situation such as expensive mobile data usage in African country where it may be worth doing but still, very limited benefit after compression.


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

This will get the app running on locally on http://localhost:9105
(May need to add in ~/.shadow-cljs cider-nrepl as dependency)

```
$ npm install
```

And repeat

```
$ npm start
```

Development server - http://localhost:9105
Dashboard for build - http://localhost:9103/dashboard

Check package.json for more details.

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
