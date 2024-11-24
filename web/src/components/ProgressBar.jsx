function ProgressBar({ percentage }) {
    const roundedPercentage = percentage.toFixed(2);
  
    return (
      <div className="progress-container">
        <progress value={percentage} max="100" className="progress-bar"></progress>
        <span className="progress-text"> {roundedPercentage} %</span>
      </div>
    );
  }
  
  export default ProgressBar;
  