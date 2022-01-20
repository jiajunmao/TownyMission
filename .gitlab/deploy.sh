VER=$(echo target/TownyMission*.jar | cut -d - -f 2)
ssh $1@colo.server.naturecraft.world "rm /data/spigot-118/plugins/TownyMission*.jar"
rsync -avzL target/TownyMission-$VER-SNAPSHOT.jar $1@colo.server.naturecraft.world:/data/spigot-118/plugins/