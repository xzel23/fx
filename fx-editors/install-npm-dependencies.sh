DIR=`dirname $0`
cd ${DIR}

npm install && cp -r node_modules/* src/main/resources/com/dua3/fx/editors/intern/
