#!/bin/sh
# ------------------------------------------------------------------------
# Copyright 2018 JPPL
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License
# ------------------------------------------------------------------------
set -e

# custom jp non-root user and group variables
user=${APP_USER}
group=${APP_USER_GRP}

# file path variables
volumes=${APP_HOME}/jppl-server-volume

# capture the Docker container IP from the container's /etc/hosts file
docker_container_ip=$(awk 'END{print $1}' /etc/hosts)

# check if the WSO2 non-root user has been created
! getent passwd ${user} >/dev/null 2>&1 && echo "Docker non-root user does not exist" && exit 1

# check if the WSO2 non-root group has been created
! getent group ${group} >/dev/null 2>&1 && echo "Docker non-root group does not exist" && exit 1

# check if the WSO2 non-root user home exists
test ! -d ${APP_HOME} && echo "JP Docker non-root user home does not exist" && exit 1

# start the server
echo "java -Dapp.home=${APP_HOME} -Dserver.port=${APP_PORT} -Djp.logging.service=${APP_NAME} -Djp.logging.version=${APP_VER} -Djava.security.egd=file:/dev/./urandom -Djava.awt.headless=true -Doracle.jdbc.fanEnabled=false -Dlog4j2.formatMsgNoLookups=True -jar ${APP_NAME}.jar --spring.config.location=application.properties"
      java -Dapp.home=${APP_HOME} -Dserver.port=${APP_PORT} -Djava -Duser.timezone=GMT+8 -Djp.logging.service=${APP_NAME} -Djp.logging.version=${APP_VER} -Djava.security.egd=file:/dev/./urandom -Djava.awt.headless=true -Doracle.jdbc.fanEnabled=false -Dlog4j2.formatMsgNoLookups=True -jar ${APP_NAME}.jar --spring.config.location=application.properties
