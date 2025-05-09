-- MySQL dump 10.13  Distrib 8.0.11, for Win64 (x86_64)
--
-- Host: y5s2h87f6ur56vae.cbetxkdyhwsb.us-east-1.rds.amazonaws.com    Database: v8aaakav2nzn5tmh
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8mb4 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `affiliations`
--

DROP TABLE IF EXISTS `affiliations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `affiliations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `affiliations`
--

/*!40000 ALTER TABLE `affiliations` DISABLE KEYS */;
INSERT INTO `affiliations` VALUES (1,'チームX'),(2,'チームY');
/*!40000 ALTER TABLE `affiliations` ENABLE KEYS */;

--
-- Table structure for table `departments`
--

DROP TABLE IF EXISTS `departments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `departments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `departments`
--

/*!40000 ALTER TABLE `departments` DISABLE KEYS */;
INSERT INTO `departments` VALUES (3,'SSK'),(5,'EQUIOS 開発チーム');
/*!40000 ALTER TABLE `departments` ENABLE KEYS */;

--
-- Table structure for table `employees`
--

DROP TABLE IF EXISTS `employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `employees` (
  `delete_flg` tinyint NOT NULL,
  `department_id` int NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `code` varchar(10) NOT NULL,
  `first_name` varchar(20) NOT NULL,
  `last_name` varchar(20) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(10) NOT NULL,
  PRIMARY KEY (`code`),
  KEY `FKgy4qe3dnqrm3ktd76sxp7n4c2` (`department_id`),
  CONSTRAINT `FKgy4qe3dnqrm3ktd76sxp7n4c2` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees`
--

/*!40000 ALTER TABLE `employees` DISABLE KEYS */;
INSERT INTO `employees` VALUES (0,3,'2025-04-10 00:15:57.492473','2025-04-10 00:15:57.492473','2839','智朗','鈴村','suzumura2839@ssk-kan.co.jp','$2a$10$0XMzAPg5mJYHsH9SazCNfeAzSbKk2ZdRufhvJ6lvVlpoz3N6q3jZ.','ADMIN'),(0,5,'2025-04-04 02:05:06.985629','2025-04-04 02:13:12.125833','2930','拓帆','鹿児島','kagoshima2930@ssk-kan.co.jp','$2a$10$NpSmZBR2x08e0ExiEbFdoOCiAFW/wBvk.YYSvWAM6jtNv2Lb0qN4.','GENERAL'),(1,5,'2025-04-02 10:11:45.000000','2025-04-18 02:57:33.306903','9997','次郎','渡辺','takku1226000@gmail.com','$2a$10$8l0S8yqGya5.zDkg6CQTKeOGoCzfyFsOHXirFN/tvRfBATrIstHqW','GENERAL'),(1,3,'2025-04-02 10:11:45.000000','2025-04-18 02:57:26.732874','9998','太郎','田中','kagoshima2930@ssk-kan.co.jp','$2a$10$l16EO7683g5HY3KZk9Uh8e2cvj7Ipn7LQszprAHlj1EceATk8kGYS','GENERAL'),(0,3,'2025-04-02 10:11:45.000000','2025-04-02 15:10:28.085263','9999','A','管理者','takku1226000@gmail.com','$2a$10$PpSqsG6kVXXSBXDzx5h5d..YrRuUEEjfifp3SWVU5pJRA1qOfbUdS','ADMIN');
/*!40000 ALTER TABLE `employees` ENABLE KEYS */;

--
-- Table structure for table `reports`
--

DROP TABLE IF EXISTS `reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `reports` (
  `approval_flg` tinyint DEFAULT NULL,
  `complete_flg` tinyint NOT NULL,
  `delete_flg` tinyint NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `rate_business` int NOT NULL,
  `rate_study` int NOT NULL,
  `report_deadline` date NOT NULL,
  `time_over` int NOT NULL,
  `time_worked` int NOT NULL,
  `trend_business` int NOT NULL,
  `submitted_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `employee_code` varchar(10) NOT NULL,
  `comment` longtext,
  `content_company` longtext,
  `content_customer` longtext,
  `content_member` longtext,
  `content_others` longtext,
  `content_problem` longtext,
  `evaluation_business` longtext,
  `evaluation_study` longtext,
  `goal_business` longtext,
  `goal_study` longtext,
  `content_business` longtext,
  `report_month` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKml3syulw48umvbjfdaeqawq3b` (`employee_code`),
  CONSTRAINT `FKml3syulw48umvbjfdaeqawq3b` FOREIGN KEY (`employee_code`) REFERENCES `employees` (`code`),
  CONSTRAINT `reports_chk_1` CHECK (((`rate_business` >= 0) and (`rate_business` <= 999))),
  CONSTRAINT `reports_chk_2` CHECK (((`rate_study` >= 0) and (`rate_study` <= 999))),
  CONSTRAINT `reports_chk_3` CHECK (((`time_over` >= 0) and (`time_over` <= 200))),
  CONSTRAINT `reports_chk_4` CHECK (((`time_worked` >= 0) and (`time_worked` <= 200))),
  CONSTRAINT `reports_chk_5` CHECK (((`trend_business` >= 0) and (`trend_business` <= 999)))
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reports`
--

/*!40000 ALTER TABLE `reports` DISABLE KEYS */;
INSERT INTO `reports` VALUES (NULL,0,0,1,100,90,'2024-10-31',10,160,80,'2025-04-02 10:11:45.000000','2025-04-02 10:11:45.000000','9999','comment サンプルテキスト','会社関係 サンプルテキスト','お客様情報 サンプルテキスト','その他メンバー関連内容 サンプルテキスト','その他 サンプルテキスト','問題点内容 サンプルテキスト','自己評価（先月業務目標） サンプルテキスト','自己評価（先月学習目標） サンプルテキスト','今月業務目標 サンプルテキスト','今月学習目標 サンプルテキスト','業務内容 サンプルテキスト','2025-02'),(NULL,1,1,2,100,90,'2024-10-31',10,160,80,'2025-04-02 10:11:45.000000','2025-04-18 02:57:26.760943','9998','comment サンプルテキスト','会社関係 サンプルテキスト','お客様情報 サンプルテキスト','その他メンバー関連内容 サンプルテキスト','その他 サンプルテキスト','問題点内容 サンプルテキスト','自己評価（先月業務目標） サンプルテキスト','自己評価（先月学習目標） サンプルテキスト','今月業務目標 サンプルテキスト','今月学習目標 サンプルテキスト','業務内容 サンプルテキスト','2025-02'),(NULL,1,1,3,100,90,'2024-10-31',10,160,80,'2025-04-02 10:11:45.000000','2025-04-18 02:57:33.310933','9997','','会社関係 サンプルテキスト','お客様情報 サンプルテキスト','その他メンバー関連内容 サンプルテキスト','その他 サンプルテキスト','問題点内容 サンプルテキスト','自己評価（先月業務目標） サンプルテキスト','自己評価（先月学習目標） サンプルテキスト','今月業務目標 サンプルテキスト','今月学習目標 サンプルテキスト','業務内容 サンプルテキスト','2025-02'),(NULL,0,1,4,100,90,'2024-09-30',10,160,80,'2025-04-02 10:11:45.000000','2025-04-18 02:57:33.311045','9997','','会社関係 サンプルテキスト','お客様情報 サンプルテキスト','その他メンバー関連内容 サンプルテキスト','その他 サンプルテキスト','問題点内容 サンプルテキスト','自己評価（先月業務目標） サンプルテキスト','自己評価（先月学習目標） サンプルテキスト','今月業務目標 サンプルテキスト','今月学習目標 サンプルテキスト','業務内容 サンプルテキスト','2025-02'),(NULL,0,1,5,100,90,'2024-08-31',10,160,80,'2025-04-02 10:11:45.000000','2025-04-18 02:57:33.311163','9997','comment サンプルテキスト','会社関係 サンプルテキスト','お客様情報 サンプルテキスト','その他メンバー関連内容 サンプルテキスト','その他 サンプルテキスト','問題点内容 サンプルテキスト','自己評価（先月業務目標） サンプルテキスト','自己評価（先月学習目標） サンプルテキスト','今月業務目標 サンプルテキスト','今月学習目標 サンプルテキスト','業務内容 サンプルテキスト','2024-08'),(NULL,0,1,6,100,90,'2024-07-31',10,160,80,'2025-04-02 10:11:45.000000','2025-04-18 02:57:33.311284','9997','comment サンプルテキスト','会社関係 サンプルテキスト','お客様情報 サンプルテキスト','その他メンバー関連内容 サンプルテキスト','その他 サンプルテキスト','問題点内容 サンプルテキスト','自己評価（先月業務目標） サンプルテキスト','自己評価（先月学習目標） サンプルテキスト','今月業務目標 サンプルテキスト','今月学習目標 サンプルテキスト','業務内容 サンプルテキスト','2024-07'),(NULL,0,1,7,100,90,'2024-06-30',10,160,80,'2025-04-02 10:11:45.000000','2025-04-18 02:57:33.311406','9997','comment サンプルテキスト','会社関係 サンプルテキスト','お客様情報 サンプルテキスト','その他メンバー関連内容 サンプルテキスト','その他 サンプルテキスト','問題点内容 サンプルテキスト','自己評価（先月業務目標） サンプルテキスト','自己評価（先月学習目標） サンプルテキスト','今月業務目標 サンプルテキスト','今月学習目標 サンプルテキスト','業務内容 サンプルテキスト','2024-06'),(NULL,0,1,8,100,90,'2024-05-31',10,160,80,'2025-04-02 10:11:45.000000','2025-04-18 02:57:33.311555','9997','comment サンプルテキスト','会社関係 サンプルテキスト','お客様情報 サンプルテキスト','その他メンバー関連内容 サンプルテキスト','その他 サンプルテキスト','問題点内容 サンプルテキスト','自己評価（先月業務目標） サンプルテキスト','自己評価（先月学習目標） サンプルテキスト','今月業務目標 サンプルテキスト','今月学習目標 サンプルテキスト','業務内容 サンプルテキスト','2024-05'),(1,0,0,9,70,70,'2025-04-30',1,152,100,NULL,'2025-04-23 10:38:20.731336','2930','','特にありません。','お客様先のデスクの数に限りがあるため、EQUIOSメンバーで在宅勤務の日を決めています。（木村さんは研修中の為、出社しています。）\r\n常に木村さんと他のメンバーの誰かは出社している状態になるようにしています。','田井社員\r\n基本在宅、月に数回出社されています。\r\n業務内で不明点がある場合、質問させていただいています。\r\n\r\n中村社員\r\n基本出社、月に数回在宅勤務されています。\r\n\r\n生田社員\r\n不明点があれば都度、質問していただいています。\r\n\r\n木村社員\r\n新人研修を行っています。\r\nC++の基礎を参考書で学習したのち、現在は研修用のアプリケーションの実装を行っています。\r\n基礎的な実装は問題なくできています。\r\nオブジェクト指向プログラミングの勉強会に参加していただきました。\r\n研修の進捗は順調だと思います。','特にありません。','【問題点】\r\nSourceTree（ソースコードバージョン管理ツール）で、チェックアウトできない。\r\n【解決策】\r\nファイルのパーミッションの変更をしないように設定を追加すると、治りました。（詳しい原因はよくわかっていません。）\r\n【ヒヤリハット】','EQUIOS知識を深めることができました。\r\nスケジュール通りに作業を進めることで、基本残業をすることなく、業務を行うことができました。','Angularのチュートリアルをしました。','EQUIOSに関する理解を広げる。\r\nスケジュール通りに作業を進めることができるようにする。','Angularを触る。','【EQUIOS】\r\n開発言語\r\n・Java\r\n\r\n業務内容\r\n・定例会議（毎週火曜）\r\n・EQUIOS勉強会&KPT（月１回）\r\n月に一回のEQUIOS 勉強会では、1か月の業務で得た知識をそれぞれ共有しています。\r\nより広い範囲の知識を身に着けることが目的です。\r\n今月は木村さんが入ってこられたこともあり、EQUIOS関連の基礎知識を田井さんにご説明いただきました。\r\n\r\n・#11830_POD連携_扉ページ指定 トラブル対応\r\n(FLC-25-0058, FLC-25-0059, FLC-25-0060, FLC-25-0061, FLC-25-0067, FLC-25-0074)\r\n扉ページ指定対応の内部トラブルの修正（調査・実装・評価）を行いました。\r\nトラブルすべて修正済みです。\r\n\r\n・#11803_POD部数印刷時のバーコード発生 評価\r\n制御部の評価を実施しました。\r\nNG を1件報告済みです。\r\n\r\n・新人研修 ソースコードレビュー オブジェクト指向プログラミングの勉強会\r\n木村さんの研修で実装されたソースコードのレビューを行いました。\r\n研修の期間は十分にあるため、細かいところまでレビュー指摘できるようにしています。\r\nまた、オブジェクト指向プログラミングの勉強会を実施しました。（木村さん、中村さん、生田さん参加）','2025-04'),(NULL,0,1,10,100,90,'2025-04-30',10,160,80,NULL,'2025-04-23 11:47:55.522259','9999','','会社関係 サンプルテキスト','お客様情報 サンプルテキスト','その他メンバー関連内容 サンプルテキスト','その他 サンプルテキスト','問題点内容 サンプルテキスト','自己評価（先月業務目標） サンプルテキスト','自己評価（先月学習目標） サンプルテキスト','今月業務目標 サンプルテキスト','今月学習目標 サンプルテキスト','業務内容 サンプルテキスト','2025-04'),(NULL,0,0,11,100,90,'2025-04-30',10,160,80,NULL,'2025-04-24 02:56:35.848187','9999','','会社関係 サンプルテキスト','お客様情報 サンプルテキスト','その他メンバー関連内容 サンプルテキスト','その他 サンプルテキスト','問題点内容 サンプルテキスト','自己評価（先月業務目標） サンプルテキスト','自己評価（先月学習目標） サンプルテキスト','今月業務目標 サンプルテキスト','今月学習目標 サンプルテキスト','業務内容 サンプルテキスト','2025-04');
/*!40000 ALTER TABLE `reports` ENABLE KEYS */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-25 21:47:41
