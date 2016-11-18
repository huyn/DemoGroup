package com.huyn.demogroup.particle.leonids.initializers;

import com.huyn.demogroup.particle.leonids.Particle;

import java.util.Random;

public interface ParticleInitializer {

	void initParticle(Particle p, Random r);

}
