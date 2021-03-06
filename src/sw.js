importScripts(
  "https://storage.googleapis.com/workbox-cdn/releases/5.1.2/workbox-sw.js"
);

const { createHandlerBoundToURL, precacheAndRoute } = workbox.precaching;
const { NavigationRoute, registerRoute } = workbox.routing;
const { CacheFirst, StaleWhileRevalidate } = workbox.strategies;
const { CacheableResponse } = workbox.cacheableResponse;

if (workbox) {
  console.log(`Workbox is loaded 🎉`);

  // Workbox will automatically avoid the browser's HTTP cache if there is out-of-band revision information provided for a given entry in the precache manifest—it uses that as a signal that the contents of the URL might change over time.
  precacheAndRoute(self.__WB_MANIFEST);

  // External resources pulled in HTML Head
  registerRoute(
    ({ url }) =>
      url.origin === "https://cdnjs.cloudflare.com" ||
      url.origin === "https://fonts.googleapis.com" ||
      url.origin === "https://fonts.gstatic.com" ||
      url.origin === "https://maxst.icons8.com",
    new StaleWhileRevalidate({
      cacheName: "pomo-static-external",
    })
  );

  // Cache static
  registerRoute(
    ({ url }) =>
      url.origin === self.location.origin &&
      url.pathname.startsWith("/static/"),
    new StaleWhileRevalidate({
      cacheName: "pomo-static-local",
    })
  );

// For SPA (single page app), React for this case. Makes navigation work.
// This assumes /index.html has been precached.
// https://developers.google.com/web/tools/workbox/modules/workbox-routing#how_to_register_a_navigation_route
const handler = createHandlerBoundToURL('/index.html');
const navigationRoute = new NavigationRoute(handler);
registerRoute(navigationRoute);


} else {
  console.log(`Error! Workbox didn't load 😬`);
}
