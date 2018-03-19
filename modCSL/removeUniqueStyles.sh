#!/bin/bash
declare -A InTarget

while read path
do
      name=${path##*/}
          InTarget[$name]=$path
        done < <(find $1 -type f)

        while read path
        do
              name=${path##*/}
                  [[ -z ${InTarget[$name]} ]] && rm -f $path
                done < <(find $2 -type f)
