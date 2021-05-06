#!/usr/bin/env bash

$KAFKA_HOME/bin/kafka-topics.sh --create --topic $1 --bootstrap-server localhost:9092
