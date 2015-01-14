-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Jan 14, 2015 at 05:58 PM
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

--
-- Dumping data for table `gamecontent`
--

INSERT INTO `gamecontent` (`id`, `title`) VALUES
(1, 'A list of testquestions');

--
-- Dumping data for table `multi_answer`
--

INSERT INTO `multi_answer` (`questionId`, `choiceId`, `answer`) VALUES
(1, 0, 'yes'),
(1, 1, 'no');

--
-- Dumping data for table `players`
--

INSERT INTO `players` (`id`, `username`, `passwordhash`, `email`, `realname`) VALUES
(1, 'christina', 'notahashyet', 'christina.korosec@student.pxl.be', 'Christina Lena Korosec');

--
-- Dumping data for table `player_games`
--

INSERT INTO `player_games` (`playerId`, `gameContentId`, `score`) VALUES
(1, 1, 90);

--
-- Dumping data for table `question`
--

INSERT INTO `question` (`id`, `type`, `question`, `text_answer`, `multi_answer`, `extraInfo`, `content_url`, `gamecontentId`, `latitude`, `longitude`) VALUES
(1, 1, 'Is this a testquestion?', NULL, 0, 'Yes, this was a testquestion, since it''s in the test gamecontent!', 'http://public.ds003.info/imghost/testcontent.png', 1, 50.9167, 5.3333),
(2, 0, 'This is another testquestion. To answer this question correctly, fill out the opposite word of ''no'' in the answer.', 'yes', NULL, 'And this was the second testquestion, this one in plain text!', 'http://public.ds003.info/imghost/testcontent.png', 1, 50.9368583, 5.3483892);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
