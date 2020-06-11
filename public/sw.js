importScripts(
  "https://storage.googleapis.com/workbox-cdn/releases/5.1.2/workbox-sw.js"
);

const { precacheAndRoute } = workbox.precaching;
const { registerRoute } = workbox.routing;
const { CacheFirst, StaleWhileRevalidate } = workbox.strategies;
const { CacheableResponse } = workbox.cacheableResponse;

if (workbox) {
  console.log(`Yay! Workbox is loaded 🎉`);

  precacheAndRoute(self.__WB_MANIFEST);

  // precacheAndRoute([
  //   {"revision":"f2a1451f77b3d98fa61f908d817b263d","url":"css/main_style.css"},
  //   {"revision":"7b53b0f6218d83d6453993fe1d085745","url":"css/tailwind.css"},
  //   {"revision":"8839e38f6d85b3ca7370e8c2e243230c","url":"index.html"},
  //   {"revision":"eea321bd24a98f0b8c30483a6b8d6205","url":"js/main.js"},
  //   // {"revision":"eea321bd24a98f0b8c304834b8d6205","url":"clock"},
  //   // {"revision":"eea321ba24a98f0b8c30483a6b8d6205","url":"clock/"},
  //   // {"revision":"eea321bd24a9cf0b8c30483a6b8d6205","url":"/"}
  // ]);

  // precacheAndRoute([
  //   { url: "/", revision: "1" },
  //   { url: "/index.html", revision: "1" },
  //   { url: "/clock", revision: "1" },
  //   { url: "/timer", revision: "1" },
  //   { url: "/pomodoro", revision: "1" },
  // ]);

  registerRoute(
    ({ url }) =>
      url.origin === "https://fonts.googleapis.com" ||
      url.origin === "https://fonts.gstatic.com",
    new StaleWhileRevalidate({
      cacheName: "server-google-fonts",
    })
  );

  // // Cache static (TODO: Not required currently)
  registerRoute(
    ({ url }) =>
      url.origin === self.location.origin &&
      url.pathname.startsWith("/static/"),
    new StaleWhileRevalidate({
      cacheName: "server-local-static",
    })
  );

} else {
  console.log(`Boo! Workbox didn't load 😬`);
}
