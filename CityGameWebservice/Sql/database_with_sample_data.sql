-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Feb 22, 2015 at 03:08 AM
-- Server version: 5.6.21
-- PHP Version: 5.6.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `citygame`
--

-- --------------------------------------------------------

--
-- Table structure for table `gamecontent`
--

CREATE TABLE IF NOT EXISTS `gamecontent` (
`id` int(11) NOT NULL COMMENT 'unique id for gamecontent',
  `title` varchar(50) NOT NULL COMMENT 'title such as city name'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `gamecontent`
--

INSERT INTO `gamecontent` (`id`, `title`) VALUES
(1, 'A list of testquestions');

-- --------------------------------------------------------

--
-- Table structure for table `multi_answer`
--

CREATE TABLE IF NOT EXISTS `multi_answer` (
  `qid` int(11) NOT NULL,
  `gid` int(11) NOT NULL,
  `choiceId` int(3) NOT NULL,
  `answer` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `multi_answer`
--

INSERT INTO `multi_answer` (`qid`, `gid`, `choiceId`, `answer`) VALUES
(1, 1, 0, 'yes'),
(1, 1, 1, 'no');

-- --------------------------------------------------------

--
-- Table structure for table `players`
--

CREATE TABLE IF NOT EXISTS `players` (
`id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `passwordhash` char(60) NOT NULL COMMENT 'bcrypt',
  `email` varchar(255) NOT NULL,
  `realname` varchar(50) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `players`
--

INSERT INTO `players` (`id`, `username`, `passwordhash`, `email`, `realname`) VALUES
(1, 'christina', '$2y$10$RY6h4QeQ4Xj0togHRHXdoO26NQ/3LMgKAvkEQAb8HwZLTQbkxFG8y', 'christina.korosec@student.pxl.be', 'Christina Lena Korosec');

-- --------------------------------------------------------

--
-- Table structure for table `player_games`
--

CREATE TABLE IF NOT EXISTS `player_games` (
  `playerId` int(11) NOT NULL,
  `gameContentId` int(11) NOT NULL,
  `score` float NOT NULL COMMENT 'Earned score when played'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `player_games`
--

INSERT INTO `player_games` (`playerId`, `gameContentId`, `score`) VALUES
(1, 1, 2);

-- --------------------------------------------------------

--
-- Table structure for table `player_question`
--

CREATE TABLE IF NOT EXISTS `player_question` (
  `pid` int(11) NOT NULL COMMENT 'player id',
  `gid` int(11) NOT NULL COMMENT 'game id',
  `qid` int(11) NOT NULL COMMENT 'question id',
  `answer` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `player_question`
--

INSERT INTO `player_question` (`pid`, `gid`, `qid`, `answer`) VALUES
(1, 1, 1, '0'),
(1, 1, 2, 'yes');

-- --------------------------------------------------------

--
-- Table structure for table `question`
--

CREATE TABLE IF NOT EXISTS `question` (
  `id` int(11) NOT NULL,
  `gid` int(11) NOT NULL,
  `type` int(1) NOT NULL DEFAULT '0',
  `question` text NOT NULL,
  `text_answer` varchar(255) DEFAULT NULL,
  `multi_answer` int(3) DEFAULT NULL,
  `placename` text NOT NULL COMMENT 'Straatnaam, wegbeschrijving, ...',
  `extraInfo` text NOT NULL,
  `content_url` varchar(255) DEFAULT NULL COMMENT 'image or video url',
  `latitude` double NOT NULL,
  `longitude` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `question`
--

INSERT INTO `question` (`id`, `gid`, `type`, `question`, `text_answer`, `multi_answer`, `placename`, `extraInfo`, `content_url`, `latitude`, `longitude`) VALUES
(1, 1, 1, 'Is this a testquestion?', NULL, 0, 'ergens in Hasselt', 'Yes, this was a testquestion, since it''s in the test gamecontent!', 'http://public.ds003.info/vid/small/vid.webm', 50.9167, 5.3333),
(2, 1, 0, 'This is another testquestion. To answer this question correctly, fill out the opposite word of ''no'' in the answer.', 'yes', NULL, 'ergens anders in Hasselt :P', 'And this was the second testquestion, this one in plain text!', 'http://public.ds003.info/imghost/genk1.jpg', 50.9368583, 5.3483892);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `gamecontent`
--
ALTER TABLE `gamecontent`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `multi_answer`
--
ALTER TABLE `multi_answer`
 ADD PRIMARY KEY (`qid`,`gid`,`choiceId`), ADD KEY `gid` (`gid`);

--
-- Indexes for table `players`
--
ALTER TABLE `players`
 ADD PRIMARY KEY (`id`), ADD UNIQUE KEY `username` (`username`), ADD KEY `username_2` (`username`);

--
-- Indexes for table `player_games`
--
ALTER TABLE `player_games`
 ADD PRIMARY KEY (`playerId`,`gameContentId`), ADD KEY `gameContentId` (`gameContentId`);

--
-- Indexes for table `player_question`
--
ALTER TABLE `player_question`
 ADD PRIMARY KEY (`pid`,`gid`,`qid`), ADD KEY `gid` (`gid`), ADD KEY `qid` (`qid`);

--
-- Indexes for table `question`
--
ALTER TABLE `question`
 ADD PRIMARY KEY (`id`,`gid`), ADD KEY `gid` (`gid`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `gamecontent`
--
ALTER TABLE `gamecontent`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'unique id for gamecontent',AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `players`
--
ALTER TABLE `players`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `multi_answer`
--
ALTER TABLE `multi_answer`
ADD CONSTRAINT `multi_answer_ibfk_1` FOREIGN KEY (`qid`) REFERENCES `question` (`id`),
ADD CONSTRAINT `multi_answer_ibfk_2` FOREIGN KEY (`gid`) REFERENCES `gamecontent` (`id`);

--
-- Constraints for table `player_games`
--
ALTER TABLE `player_games`
ADD CONSTRAINT `player_games_ibfk_1` FOREIGN KEY (`gameContentId`) REFERENCES `gamecontent` (`id`),
ADD CONSTRAINT `player_games_ibfk_2` FOREIGN KEY (`playerId`) REFERENCES `players` (`id`);

--
-- Constraints for table `player_question`
--
ALTER TABLE `player_question`
ADD CONSTRAINT `player_question_ibfk_1` FOREIGN KEY (`pid`) REFERENCES `player_games` (`playerId`),
ADD CONSTRAINT `player_question_ibfk_2` FOREIGN KEY (`gid`) REFERENCES `gamecontent` (`id`),
ADD CONSTRAINT `player_question_ibfk_3` FOREIGN KEY (`qid`) REFERENCES `question` (`id`);

--
-- Constraints for table `question`
--
ALTER TABLE `question`
ADD CONSTRAINT `question_ibfk_1` FOREIGN KEY (`gid`) REFERENCES `gamecontent` (`id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
