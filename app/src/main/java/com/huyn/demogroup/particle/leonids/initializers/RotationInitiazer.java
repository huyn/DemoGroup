package com.huyn.demogroup.particle.leonids.initializers;

import com.huyn.demogroup.particle.leonids.Particle;

import java.util.Random;

public class RotationInitiazer implements ParticleInitializer {

	private int mMinAngle;
	private int mMaxAngle;

	public RotationInitiazer(int minAngle, int maxAngle) {
		mMinAngle = minAngle;
		mMaxAngle = maxAngle;
	}

	@Override
	public void initParticle(Particle p, Random r) {
		int value = mMinAngle == mMaxAngle ? mMinAngle : r.nextInt(mMaxAngle-mMinAngle)
				+mMinAngle;
		p.mInitialRotation = value;
	}

}
