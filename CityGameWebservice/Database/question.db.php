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
		$questionList = array();
		$result = $this->database->query('SELECT `id`, `type`, `question`, `text_answer`, `multi_answer`, `extraInfo`, `content_url`, `gamecontentId` 
			FROM `question` WHERE `gamecontentId` = ' . $contentId);
		
		while($row = $result->fetch_assoc())
		{
			$qType = $row['type'];
			$qQuestion = $row['question'];
			if( $type == 0 ) // plain text question
			{
				$qAnswer = $row['text_answer'];
				$qExtraInfo = $row['extraInfo'];
				$qContentUrl = $row['content_url'];
				$question = new Question($qType, $qQuestion, $qAnswer);
				$question->extraInfo = $qExtraInfo;
				$question->contentUrl = $qContentUrl;
				
				$questionList[] = $question;
			}
			else if( $type == 1 ) // multiple choice question
			{
				$qAnswer = $row['multi_answer'];
				$qExtraInfo = $row['extraInfo'];
				$qContentUrl = $row['content_url'];
				$qId = $row['id'];
				$qOptions = array();
				$resultOptions = $this->database->query('SELECT `questionId`, `choiceId`, `answer` FROM `multi_answer` WHERE `questionId` = ' . 
					$qId . ' ORDER BY `choiceId`'); // Ordering is important here
				while($optionRow = $resultOptions->fetch_assoc())
				{
					$qOptions[] = $optionRow['answer'];
				}
				$question = new Question($qType, $qQuestion, $qAnswer, $qOptions);
				$question->extraInfo = $qExtraInfo;
				$question->contentUrl = $qContentUrl;
				
				$questionList[] = $question;
			}
		}
		
		return $questionList;
	}
}
?>