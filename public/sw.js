importScripts('https://storage.googleapis.com/workbox-cdn/releases/3.5.0/workbox-sw.js');

if (workbox) {
  console.log(`Workbox is loaded`);

  workbox.core.skipWaiting();
  // workbox.precaching.precacheAndRoute([]);
  // workbox.precaching.precacheAndRoute([
  //   {url: '/index.html', revision: '1' },
  // ]);
  workbox.precaching.precacheAndRoute(self.__WB_MANIFEST);

//   cacheFirst
//   workbox.routing.registerRoute(
//     /(.*)articles(.*)\.(?:png|gif|jpg)/,
//     workbox.strategies.cacheFirst({
//       cacheName: 'images-cache',
//       plugins: [
//         new workbox.expiration.Plugin({
//           maxEntries: 50,
//           maxAgeSeconds: 30 * 24 * 60 * 60, // 30 Days
//         })
//       ]
//     })
//   );

//   networkFirst
//   const articleHandler = workbox.strategies.networkFirst({
//     cacheName: 'articles-cache',
//     plugins: [
//       new workbox.expiration.Plugin({
//         maxEntries: 50,
//       })
//     ]
//   });
  
  // workbox.routing.registerRoute(/(.*)article(.*)\.html/, args => {
  //   return articleHandler.handle(args).then(response => {
  //       if (!response) {
  //         return caches.match('pages/offline.html');
  //       } else if (response.status === 404) {
  //         return caches.match('pages/404.html');
  //       }
  //       return response;
  //     });;
  // });

} else {
  console.log(`Workbox didn't load`);
}