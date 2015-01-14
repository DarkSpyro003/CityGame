<?php
class Question
{
	private $type;
	private $question
	private $text_answer;
	private $multi_answer;
	private $options = array();
	private $extraInfo;
	private $contentUrl;
	
	// Constructor plain text question
	public __construct($type, $question, $answer)
	{
		if( $type != 0 )
			throw new Exception('Type must be 0 for using this constructor');
		
		$this->type = $type;
		$this->question = $question;
		$this->text_answer = $answer;
	}
	
	// Constructor multiple choice question
	public __construct($type, $question, $answer, $options)
	{
		if( $type != 1 )
			throw new Exception('Type must be 1 for using this constructor');
		
		$this->type = $type;
		$this->question = $question;
		$this->multi_answer = $answer;
		$this->options = $options;
	}
	
	public function __get($property) 
	{
		if (property_exists($this, $property))
		{
			return $this->$property;
		}
	}

	public function __set($property, $value)
	{
		if (property_exists($this, $property)) 
		{
			$this->$property = $value;
		}
		return $this;
		}
	}
?>