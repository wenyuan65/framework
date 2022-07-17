drop table if exists `player`;
create table `player` (
	`id` int(10) unsigned not null auto_increment comment 'id',
	`player_id` bigint(20) unsigned not null comment '玩家id',
	`player_name` varchar(20) not null default '' comment '名称',
	`lv` int(10) unsigned not null default '0' comment '等级',
	`male` tinyint(4) unsigned not null default '1' comment '性别，1男2女',
	`pic` varchar(20) not null default '' comment '角色图片',
	`yx` varchar(20) not null default '' comment '联运',
	`channel_id` varchar(50) not null default '' comment '渠道',
	`yx_source` varchar(50) not null default '' comment '来源',
	`user_id` varchar(50) not null default '' comment '玩家联运标识',
	primary key(`id`),
	index `idx_playerId` (`player_id`),
	index `idx_yx_userId` (`yx`, `user_id`)
);

insert into `player` (`player_id`, `player_name`, `lv`, `male`, `pic`, `yx`, `channel_id`, `yx_source`,`user_id`) values 
(1001, '测试1', '1', '1', 'nanzhujue', 'panda', 'and_panda', 'panda', 'pd012123123123'), 
(1002, '测试2', '1', '1', 'nanzhujue', 'panda', 'and_panda', 'panda', 'pd012123123124');
