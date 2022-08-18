##################################################
# proxy-service-1.0.5 | 18-August-2022
##################################################
1. add new enum CoverImageUrl for storeAssets (note: alter table notes in product service)

##################################################
# proxy-service-1.0.4 | 15-July-2022
##################################################
ALTER TABLE symplified.platform_og_tag ADD property varchar(100) NULL;
ALTER TABLE symplified.platform_og_tag ADD content varchar(500) NULL;
ALTER TABLE symplified.platform_og_tag ADD name varchar(100) NULL;
ALTER TABLE symplified.platform_og_tag ADD platformId varchar(50) NULL;

ALTER TABLE symplified.platform_og_tag ADD CONSTRAINT platform_og_tag_FK FOREIGN KEY (platformId) REFERENCES symplified.platform_config(platformId) ON DELETE RESTRICT ON UPDATE RESTRICT;

##################################################
# proxy-service-1.0.3 | 8-July-2022
##################################################
1. Print log

##################################################
# proxy-service-1.0.2 | 7-July-2022
##################################################

CREATE TABLE `platform_og_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `imageUrl` varchar(500) DEFAULT NULL,
  `platformType` varchar(100) DEFAULT NULL,
  `regionCountryId` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;

##################################################
# proxy-service-1.0.1 | 4-July-2022
##################################################
1. Change url servoce for logo

##################################################
# proxy-service-0.0.1-SNAPSHOT | 22-June-2022
##################################################
1. Implement proxy service for OG meta tag
2. update whatsapp version for crawling