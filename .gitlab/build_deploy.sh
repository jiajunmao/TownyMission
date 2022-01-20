SCRIPTPATH="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"

bash $SCRIPTPATH/build.sh
bash $SCRIPTPATH/deploy.sh $1