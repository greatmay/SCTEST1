/**
 * java script simulation for frontend app
 */



/*function device_info() {
         const data = {
              app_version: "1.0",
              age: 30,
              city: "New York"
          };
        return JSON.stringify(data);  // Return JSON string to Kotlin
 }*/


function handleScreenshot(base64Image) {

	 const img = document.createElement('img');
	 img.src = base64Image;
	 img.style.width = '400px';
	 img.style.border = '10px solid yellow';
	 img.style.position = 'fixed';
	 img.style.top = '10px';
	 img.style.left = '10px';
	 img.style.zIndex = '9999';
	 img.style.transition = 'opacity 0.5s ease-out';
	 document.body.appendChild(img);
	 // Fade out and remove
	 setTimeout(() => {
	 img.style.opacity = '0';
	 setTimeout(() => {
	 img.remove();
	 }, 500); // Wait for fade animation to complete
	 }, 4500); // Start fade out after 4.5 seconds
	 }
	 
	 
	 class SlidePlayer {
	  constructor(container, slides) {
	  this.container = container;
	  this.slides = slides;
	  this.currentIndex = 0;
	  this.timer = null;
	  this.currentVideo = null;
	 //Senior Mobile Developer Challenge 9
	  this.debugPanel = document.getElementById
	 ('debugContent');
	  this.init();
	  //this.precacheContent();
	  }
  
	  init() {
	   this.slides.forEach(slide => {
	   const slideElement = document.createElement('div');
	   slideElement.className = 'slide';
	   switch(slide.contentType) {
	   case 'image':
	   const img = document.createElement('img');
	   img.src = slide.url;
	  slideElement.appendChild(img);
	  break;
	   case 'video':
	   const video = document.createElement('video');
	   video.src = slide.url;
	  video.autoplay = true;
	  video.muted = true;
	  video.playsInline = true;
	   if (slide.duration === 0) {
	   video.addEventListener('ended', () => {
		 this.trackEvent('end',
		slide);
		 this.next();
		 });
		 }
		 
		slideElement.appendChild(video);
		 break;
		 case 'link':
		 const iframe = document.createElement('iframe');
		 iframe.src = slide.url;
		slideElement.appendChild(iframe);
		 break;
		 }
		 this.container.appendChild(slideElement);
		 });
		 this.showSlide(0);
		 }

		 updateDebugInfo(currentSlide) {
		  const nextIndex = (this.currentIndex + 1) %
		 this.slides.length;
		  const nextSlide = this.slides[nextIndex];
		  let duration = currentSlide.duration;
		  if (currentSlide.contentType === 'video' &&
		 duration === 0) {
		  duration = 'Until video ends';
		  } else {
		 //Senior Mobile Developer Challenge 12
		  duration = `${duration} seconds`;
		  }
		  const debugHtml = `
		  <div class="debug-item">Current: #${currentSlide.id} (${currentSlide.contentType})</div>
		  <div class="debug-item">Duration: ${duration}</div>
		  <div class="debug-item">Next: #${nextSlide.id} (${nextSlide.contentType})</div>
		  `;
		  this.debugPanel.innerHTML = debugHtml;
		  }

		  showSlide(index) {
		   this.cleanup();
		   const slides = this.container.getElementsByClassName('slide');
		   Array.from(slides).forEach(slide => {slide.classList.remove('active');
		   const video = slide.querySelector('video');
		   if (video) {
		   video.pause();
		  video.currentTime = 0;
		   }
		   });
		   
		   slides[index].classList.add('active');
		    const currentSlide = this.slides[index];
		    const currentElement = slides[index];
		    if (currentSlide.contentType === 'video') {
		   //Senior Mobile Developer Challenge 13
		    const video = currentElement.querySelector('video');
		    video.play();
		    this.currentVideo = video;
		    }

			this.updateDebugInfo(currentSlide);
			 this.trackEvent('start', currentSlide);
			 this.trackEvent('playing', currentSlide);
			 if (currentSlide.duration > 0) {
			 this.timer = setTimeout(() => {
			 this.trackEvent('end', currentSlide);
			 this.next();
			 }, currentSlide.duration * 1000);
			 }
			 }

			 cleanup() {
			  if (this.timer) {
			  clearTimeout(this.timer);
			  this.timer = null;
			  }
			  if (this.currentVideo) {
			  this.currentVideo.pause();
			  this.currentVideo = null;
			  }
			  }
			  
			  next() {
			   this.currentIndex = (this.currentIndex + 1)
			  % this.slides.length;
			   this.showSlide(this.currentIndex);
			   }
			   
			   trackEvent(eventType, slide) {
			    // console.log('Slide Event:', {
			    // type: eventType,
			    // slideId: slide.id,
			    // contentType: slide.contentType,
			    // timestamp: new Date().toISOString()
			    // });
			    }
			    }

				const slides = [
				 {
				 id: 1,
				 contentType: "image",
				 url: "https://picsum.photos/id/1/800/600",
				 duration: 10 // in second unit
				 },
				 {
				 id: 2,
				 contentType: "video",
				 url: "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/720/Big_Buck_Bunny_720_10s_1MB.mp4",
				 duration: 0
				 },
				 {
				 id: 3,
				 contentType: "image",
				 url: "https://picsum.photos/id/2/800/600",
				 duration: 5
				 },
				 {
				 id: 4,
				 contentType: "video",
				 url: "https://test-videos.co.uk/vids/jellyfish/mp4/h264/720/Jellyfish_720_10s_1MB.mp4",
				 duration: 5
  
				 
				 }
				  ];
				  
				  const container = document.getElementById('slideContainer');
				  const player = new SlidePlayer(container, slides);
				  document.addEventListener('DOMContentLoaded', function() {
				  // Get and display the name
				  if (window.SC_INTERFACE) {
				  const deviceName = SC_INTERFACE.get_name();
				  document.getElementById('device-name').textContent = deviceName;
				  } else {
				  document.getElementById('device-name').textContent = 'SC_INTERFACE is not available ';
				  }
				  });

				
				  
