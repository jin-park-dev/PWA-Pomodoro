server {
  listen 80;
  root /home/jin/sites/pomo.kanxdoro.com;
  server_name pomo.kanxdoro.com;
  index index.html index.htm;

  access_log /var/log/nginx/pomo.kanxdoro.com.access.log;
  error_log /var/log/nginx/pomo.kanxdoro.com.error.log;

  location / {
     try_files $uri /index.html =404;
  }
}

#location /static/ {
#   autoindex on;
#   root /home/jin/sites/pomo.kanxdoro.com/static;
#}
