##################################################
# location-service-1.0.1 | 18-Feb-2022
##################################################
```
PLEASE FOLLOW BELOW FORMAT IN ORDER TO UPDATE VERSION IN RELEASE NOTE
```
1. Add new field : displayAddress in store table

##Database changes :
ALTER TABLE `store` ADD displayAddress VARCHAR(1000);

##################################################
# location-service-1.0.2 | 18-Feb-2022
##################################################
1. Add new field in discount : isExpired
if discount end date < current date, then isExpired=true



