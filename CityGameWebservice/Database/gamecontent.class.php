<?php
require_once 'question.class.php';
class GameContent
{
	private $title;
	private $questionList = array();
	
	public GameContent($title)
	{
		$this->title = $title;
	}
	
	public getTitle()
	{
		return $this->title;
	}
	
	public setTitle($title)
	{
		$this->title = $title;
	}
}
?>