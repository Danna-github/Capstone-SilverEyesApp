# CapStone

본 연구는 노인들이 일상생활에서 느끼는 불편함을 조사하고 개선점을 분석하여, 앱 개발 시 고령자의 신체적, 인지적 변화에 따른 고령 친화 설계를 목표로 한다. 나이가 들어감에 따라 점진적으로 눈의 조절력이 떨어져 가까운 글씨를 보기 힘들어지는 노화 현상(일명 노안)과 소수의 노인 문맹을 해결하기 위한 `OCR(광학문자 인식, Optical Character Recognition) 앱 개발`한다. 


앱으로 사진을 찍으면, 이미지 프로세싱을 이용하여 이미지에서 텍스트를 추출한 후에 음성으로 글을 읽어주는(TextToSpeach) 앱을 기획한다. 이미지뿐만 아니라 텐서플로우(Tensorflow)로 이미지를 인식하여 전방의 사물을 음성으로 들려준다. 예를 들어, 카페 메뉴판 보기, 부동산 계약서 작성하기, 샴푸와 린스 구분하기 등 노인분들이 일상생활에서 보이지 않는 글씨를 음성으로 들을 수 있어 편리하다. 앱 개발 후 다수의 샘플들을 적용하여 문자와 사물의 인식률을 측정하고, 인식률을 높이기 위한 이미지 전처리(openCV) 과정을 실시한다.


또한 ‘SMS receiver API’를 사용하여 수신한 문자의 내용을 앱에서 받아서 읽어주는 기능을 구현한다.


![image](https://user-images.githubusercontent.com/46625602/82409263-4e79a680-9aa8-11ea-8141-60c6894b1f49.png)
![image](https://user-images.githubusercontent.com/46625602/82409272-50dc0080-9aa8-11ea-843d-17019817845a.png)
![image](https://user-images.githubusercontent.com/46625602/82409285-55a0b480-9aa8-11ea-9469-291f0a59b1d4.png)
![image](https://user-images.githubusercontent.com/46625602/82409287-58030e80-9aa8-11ea-86ae-b88b2059254b.png)

![image](https://user-images.githubusercontent.com/46625602/84235091-3c7fa680-ab30-11ea-8fcb-b1ae03e53015.png)

