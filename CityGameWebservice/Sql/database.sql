-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Jan 14, 2015 at 02:47 PM
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `multi_answer`
--

CREATE TABLE IF NOT EXISTS `multi_answer` (
  `questionId` int(11) NOT NULL,
  `choiceId` int(3) NOT NULL,
  `answer` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `question`
--

CREATE TABLE IF NOT EXISTS `question` (
`id` int(11) NOT NULL,
  `type` int(1) NOT NULL DEFAULT '0',
  `question` text NOT NULL,
  `text_answer` varchar(255) DEFAULT NULL,
  `multi_answer` int(3) DEFAULT NULL,
  `extraInfo` text NOT NULL,
  `content_url` varchar(255) DEFAULT NULL COMMENT 'image or video url',
  `gamecontentId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
 ADD PRIMARY KEY (`questionId`,`choiceId`);

--
-- Indexes for table `question`
--
ALTER TABLE `question`
 ADD PRIMARY KEY (`id`), ADD KEY `gamecontentId` (`gamecontentId`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `gamecontent`
--
ALTER TABLE `gamecontent`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'unique id for gamecontent';
--
-- AUTO_INCREMENT for table `question`
--
ALTER TABLE `question`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `multi_answer`
--
ALTER TABLE `multi_answer`
ADD CONSTRAINT `multi_answer_ibfk_1` FOREIGN KEY (`questionId`) REFERENCES `question` (`id`);

--
-- Constraints for table `question`
--
ALTER TABLE `question`
ADD CONSTRAINT `question_ibfk_1` FOREIGN KEY (`gamecontentId`) REFERENCES `gamecontent` (`id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
