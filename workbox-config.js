module.exports = {
  "globDirectory": "build/",  // tells which folder to scan for precached files
  "globPatterns": [
    "**/*.{jpg,svg,html,css,js}"
  ],
  "swDest": "build/sw.js",
  "swSrc": "src/sw.js",
  "globIgnores": [
    "../workbox-config.js"
  ],
  maximumFileSizeToCacheInBytes: 5000000,
};
