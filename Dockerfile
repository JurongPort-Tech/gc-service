# =======================================================
#
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
#
# =======================================================

FROM hardened-ubi8-openjdk8:1.0.0

# =======================================================
# set user variable
# =======================================================
ARG USER=jrpabg1
ARG USER_ID=1004
ARG USER_GROUP=jpom
ARG USER_GROUP_ID=1010
ARG USER_HOME=/home/${USER}
ARG JAVA_HOME=${USER_HOME}/java

# =======================================================
# set directory variable
# =======================================================
ARG APP_BIN_DIR=./target
ARG APP_PROP_DIR=./target/classes

# =======================================================
# set app variable
# =======================================================
ARG APP_NAME
ARG APP_VER
ARG APP_PORT
ARG APP_PKG_TYPE=jar
ARG APP_HOME=${USER_HOME}
ARG APP_PROP=application.properties

# =======================================================
# copy the app files to app home directory
# =======================================================
RUN mkdir -p ${USER_HOME}/log && chown jrpabg1:jpom ${USER_HOME}/log	
COPY --chown=jrpabg1:jpom ${APP_BIN_DIR}/${APP_NAME}.${APP_PKG_TYPE} ${USER_HOME}/
COPY --chown=jrpabg1:jpom ${APP_PROP_DIR}/${APP_PROP} ${USER_HOME}/${APP_PROP}
COPY --chown=jrpabg1:jpom ./init.sh ${USER_HOME}/init.sh
RUN chmod +x ${USER_HOME}/init.sh

# =======================================================
# set the user and work directory
# =======================================================
USER ${USER_ID}
WORKDIR ${USER_HOME}

# =======================================================
# set environment variables
# =======================================================
ENV JAVA_HOME=${JAVA_HOME} \
    PATH=$JAVA_HOME/bin:$PATH \
    APP_HOME=${APP_HOME} \
    APP_NAME=${APP_NAME} \
    APP_VER=${APP_VER} \
    APP_PORT=${APP_PORT} \
    APP_USER=${USER} \
    APP_USER_GRP=${USER_GROUP}

# =======================================================
# expose port
# =======================================================
EXPOSE ${APP_PORT}

# =======================================================
# start up script
# =======================================================
ENTRYPOINT ${APP_HOME}/init.sh
