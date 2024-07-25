#! /bin/bash

set -e
set -u

bye () {
  local msg="${1:-}"
  printf "ERROR $msg\n"
  exit 1
}

print_usage () {
  bye "usage: $0 <build_jet>    "
}

build_jet () {
  local dir="${1:-}"
  if [[ "$dir" == "" ]]; then
    bye "directory is required"
  fi
  dir="jet/$dir"
  local build_dir="$HOME/build/$dir"
  mkdir -p "$build_dir"
  GRADLE_OPTIONS="-Dorg.gradle.project.buildDir=$build_dir --project-cache-dir=$build_dir --no-daemon" make -C "$dir"
}

cmd="${1:-}"

if [[ "$cmd" == "" ]]; then
  echo "missing command"
  print_usage
  exit 1
fi

shift

case "$cmd" in
  "build_jet") build_jet "$@" ;;
esac

