# CustomHorizontalScrollview
实现了一个HorizontalScollview+ViewPager的滑动切换效果。（仿现在市场上的卡片效果）

HorizontalView 实现了ViewPager滑动同时其内部text颜色渐变、字体缓慢变大、下标跟随及下标长度变化。
ViewPager是重写ViewGroup实现的卡片页， 滑动的过渡效果。

![image](https://github.com/yfchu/CustomHorizontalScrollview/blob/master/Effect/xiaoguo.gif)   
![image](https://github.com/yfchu/CustomHorizontalScrollview/blob/master/Effect/xiaoguo1.jpg)  

/**
     * horizontalview点击时切换scrollview对应的pager
     */
    private Handler horiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CommonUrl.SCROLL_ROLL:
                    scrollView.setMovePage(msg.arg1);//点击item切换Scrollview
                    CursorFollow();
                    break;
                case CommonUrl.SETDATA:
                    int width = 0;
                    for (int i = 0; i < textViewList.size(); i++) {
                        width += textViewList.get(i).getViewWidth();
                    }
                    mHorizontalViewWidth = msg.arg1;//屏幕宽度
                    mTabItemWidth = width / textViewList.size();//item宽度
                    pageItemNumber = mHorizontalViewWidth / mTabItemWidth;//页显示item数量
                    startScroll = pageItemNumber / 2 + pageItemNumber % 2;//起始滚动item
                    endScroll = textViewList.size() - pageItemNumber + startScroll;//结束滚动item
                    horizontalView.setStartScroll(startScroll, endScroll);
                    break;
            }
            super.handleMessage(msg);
        }
    }; 
	
/**
     * Horizontalview游标跟随
     * */
    private void CursorFollow() {
        ValueAnimator anim = ValueAnimator.ofFloat(lineView.getTranslationX(),//游标当前位置
                getScrollDistance(Index, scrollView.getTargetIndex()));//目标位置
        anim.setDuration(moveAnimTime);
        anim.start();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lineView.setTranslationX((float) animation.getAnimatedValue());//设置平移过渡
            }
        });
        lineViewParams = (LinearLayout.LayoutParams) lineView.getLayoutParams();
        anim = ValueAnimator.ofFloat(lineViewParams.width,
                textViewList.get(scrollView.getTargetIndex()).getViewWidth());//游标宽度过渡
        anim.setDuration(moveAnimTime);
        anim.start();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lineViewParams.width = Math.round(Float.parseFloat(animation.getAnimatedValue().toString()));
                lineView.setLayoutParams(lineViewParams);
            }
        });
    }

	/**
     * scrollLayout滑动时触发horizontalview文字的颜色和大小渐变效果。
     */
    private Handler touchHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MotionEvent.ACTION_DOWN:
                    mXDown = (float) msg.obj;
                    mXMove = mXDown;
                    Index = scrollView.getTargetIndex();
                    /**
                     * 按下时初始化参数
                     * */
                    rollBackIndex = -1;
                    targetLeftIndex = -1;
                    moveState = -1;
                    textSelectScale = faultSelectScale;
                    textNormalScale = faultNormalScale;
                    break;
                case MotionEvent.ACTION_MOVE: //滑动操作
                    break;
                case MotionEvent.ACTION_UP: //手指弹起
                    break;
                case CommonUrl.FASTMOVE: //快速滑动时的up
                    break;
            }
            super.handleMessage(msg);
        }
    };
