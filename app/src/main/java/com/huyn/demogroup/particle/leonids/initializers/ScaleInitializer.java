package com.huyn.demogroup.particle.leonids.initializers;

import com.huyn.demogroup.particle.leonids.Particle;

import java.util.Random;

public class ScaleInitializer implements ParticleInitializer {

	private float mMaxScale;
	private float mMinScale;

	public ScaleInitializer(float minScale, float maxScale) {
		mMinScale = minScale;
		mMaxScale = maxScale;
	}

	@Override
	public void initParticle(Particle p, Random r) {
		float scale = r.nextFloat()*(mMaxScale-mMinScale) + mMinScale;
		p.mScale = scale;
	}

}
