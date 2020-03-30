```
#!/bin/bash

App='stock-web'
ProjectHome='/home/wild/stock/'$App
RunHome='/opt/stock/stock-web'
LogHome='/opt/stock/logs'
ConfigHome='/opt/stock'

cd $ProjectHome
git pull

kill -9 $(ps -ef|grep node|awk '/'$App.js'/{print $2}')

cp -rf $ProjectHome /opt/stock
cp -f $ConfigHome/*.js $RunHome

cd $RunHome
source ~/.nvm/nvm.sh
nvm use node
npm install

nohup npm start > $LogHome/$App.log 2>&1 &
```
