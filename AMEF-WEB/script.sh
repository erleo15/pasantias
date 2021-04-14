ng build --prod
sudo rm -rf /var/www/html/*
sudo cp -R dist/* /var/www/html/
sudo systemctl restart apache2
sudo chown -R usreducrowl:usreducrowl dist 
