<?php
require_once 'question.class.php';

class QuestionDb
{
	private $database;
	
	public function __construct($database)
	{
		$this->database = $database;
	}
	
	public function getQuestionListForContent($contentId)
	{
		$contentId = $this->database->real_escape_string($contentId);
		$questionList = array();
		$result = $this->database->query('SELECT `id`, `gid`, `type`, `question`, `text_answer`, `multi_answer`, `placename`, `extraInfo`, `content_url`, `latitude`, `longitude`
			FROM `question` WHERE `gid` = ' . $contentId);
		
		while($row = $result->fetch_assoc())
		{
			$question;
			$qId = $row['id'];
			$qType = $row['type'];
			$qQuestion = $row['question'];
			$qPlacename = $row['placename'];
			$qExtraInfo = $row['extraInfo'];
			$qContentUrl = $row['content_url'];
			$qLatitude = $row['latitude'];
			$qLongitude = $row['longitude'];
			if( $qType == 0 ) // plain text question
			{
				$qAnswer = $row['text_answer'];
				$question = new Question($qId, $qType, $qQuestion, $qAnswer);
			}
			else if( $qType == 1 ) // multiple choice question
			{
				$qAnswer = $row['multi_answer'];
				$qOptions = array();
				$resultOptions = $this->database->query('SELECT `qid`, `gid`, `choiceId`, `answer` FROM `multi_answer` WHERE `qid` = ' . 
					$qId . ' AND `gid` = ' . $row['gid'] . ' ORDER BY `choiceId`'); // Ordering is important here
				while($optionRow = $resultOptions->fetch_assoc())
				{
					$qOptions[] = $optionRow['answer'];
				}
				$question = new Question($qId, $qType, $qQuestion, $qAnswer, $qOptions);
			}
			$question->placename = $qPlacename;
			$question->extraInfo = $qExtraInfo;
			$question->contentUrl = $qContentUrl;
			$question->latitude = $qLatitude;
			$question->longitude = $qLongitude;
			
			$questionList[] = $question;
		}
		
		return $questionList;
	}
}
?>