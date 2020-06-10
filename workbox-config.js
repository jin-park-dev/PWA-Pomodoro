module.exports = {
  "globDirectory": "build/",
  "globPatterns": [
    "**/*.{jpg,svg,html,css}"
  ],
  "swDest": "build/sw.js",
  "swSrc": "public/sw.js",
  "globIgnores": [
    "../workbox-config.js"
  ]
};
