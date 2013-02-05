package me.itidez.plugins.icart.util;

public class IcartMinecart extends EntityMinecart {
	
	
	public IcartMinecart(World world) {
		super(world)
	}
	
	@Override
	public void j_() {
        // CraftBukkit start
        double prevX = this.locX;
        double prevY = this.locY;
        double prevZ = this.locZ;
        float prevYaw = this.yaw;
        float prevPitch = this.pitch;
        // CraftBukkit end

        if (this.g != null) {
            this.g.a();
        }

        if (this.j() > 0) {
            this.h(this.j() - 1);
        }

        if (this.getDamage() > 0) {
            this.setDamage(this.getDamage() - 1);
        }

        if (this.locY < -64.0D) {
            this.C();
        }

        if (this.h() && this.random.nextInt(4) == 0) {
            this.world.addParticle("largesmoke", this.locX, this.locY + 0.8D, this.locZ, 0.0D, 0.0D, 0.0D);
        }

        int i;

        if (!this.world.isStatic && this.world instanceof WorldServer) {
            this.world.methodProfiler.a("portal");
            MinecraftServer minecraftserver = ((WorldServer) this.world).getMinecraftServer();

            i = this.z();
            if (this.ao) {
                if (true || minecraftserver.getAllowNether()) { // CraftBukkit - multi-world should still allow teleport even if default vanilla nether disabled
                    if (this.vehicle == null && this.ap++ >= i) {
                        this.ap = i;
                        this.portalCooldown = this.ab();
                        byte b0;

                        if (this.world.worldProvider.dimension == -1) {
                            b0 = 0;
                        } else {
                            b0 = -1;
                        }

                        this.b(b0);
                    }

                    this.ao = false;
                }
            } else {
                if (this.ap > 0) {
                    this.ap -= 4;
                }

                if (this.ap < 0) {
                    this.ap = 0;
                }
            }

            if (this.portalCooldown > 0) {
                --this.portalCooldown;
            }

            this.world.methodProfiler.b();
        }

        if (this.world.isStatic) {
            if (this.j > 0) {
                double d0 = this.locX + (this.at - this.locX) / (double) this.j;
                double d1 = this.locY + (this.au - this.locY) / (double) this.j;
                double d2 = this.locZ + (this.av - this.locZ) / (double) this.j;
                double d3 = MathHelper.g(this.aw - (double) this.yaw);

                this.yaw = (float) ((double) this.yaw + d3 / (double) this.j);
                this.pitch = (float) ((double) this.pitch + (this.ax - (double) this.pitch) / (double) this.j);
                --this.j;
                this.setPosition(d0, d1, d2);
                this.b(this.yaw, this.pitch);
            } else {
                this.setPosition(this.locX, this.locY, this.locZ);
                this.b(this.yaw, this.pitch);
            }
        } else {
            this.lastX = this.locX;
            this.lastY = this.locY;
            this.lastZ = this.locZ;
            this.motY -= 0.03999999910593033D;
            int j = MathHelper.floor(this.locX);
            i = MathHelper.floor(this.locY);
            int k = MathHelper.floor(this.locZ);

            if (BlockMinecartTrack.e_(this.world, j, i - 1, k)) {
                --i;
            }

            // CraftBukkit
            double d4 = this.maxSpeed;
            double d5 = 0.0078125D;
            int l = this.world.getTypeId(j, i, k);

            if (BlockMinecartTrack.e(l)) {
                this.fallDistance = 0.0F;
                Vec3D vec3d = this.a(this.locX, this.locY, this.locZ);
                int i1 = this.world.getData(j, i, k);

                this.locY = (double) i;
                boolean flag = false;
                boolean flag1 = false;

                if (l == Block.GOLDEN_RAIL.id) {
                    flag = (i1 & 8) != 0;
                    flag1 = !flag;
                }

                if (((BlockMinecartTrack) Block.byId[l]).p()) {
                    i1 &= 7;
                }

                if (i1 >= 2 && i1 <= 5) {
                    this.locY = (double) (i + 1);
                }

                if (i1 == 2) {
                    this.motX -= d5;
                }

                if (i1 == 3) {
                    this.motX += d5;
                }

                if (i1 == 4) {
                    this.motZ += d5;
                }

                if (i1 == 5) {
                    this.motZ -= d5;
                }

                int[][] aint = matrix[i1];
                double d6 = (double) (aint[1][0] - aint[0][0]);
                double d7 = (double) (aint[1][2] - aint[0][2]);
                double d8 = Math.sqrt(d6 * d6 + d7 * d7);
                double d9 = this.motX * d6 + this.motZ * d7;

                if (d9 < 0.0D) {
                    d6 = -d6;
                    d7 = -d7;
                }

                double d10 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);

                this.motX = d10 * d6 / d8;
                this.motZ = d10 * d7 / d8;
                double d11;
                double d12;

                if (this.passenger != null) {
                    d12 = this.passenger.motX * this.passenger.motX + this.passenger.motZ * this.passenger.motZ;
                    d11 = this.motX * this.motX + this.motZ * this.motZ;
                    if (d12 > 1.0E-4D && d11 < 0.01D) {
                        this.motX += this.passenger.motX * 0.1D;
                        this.motZ += this.passenger.motZ * 0.1D;
                        flag1 = false;
                    }
                }

                if (flag1) {
                    d12 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
                    if (d12 < 0.03D) {
                        this.motX *= 0.0D;
                        this.motY *= 0.0D;
                        this.motZ *= 0.0D;
                    } else {
                        this.motX *= 0.5D;
                        this.motY *= 0.0D;
                        this.motZ *= 0.5D;
                    }
                }

                d12 = 0.0D;
                d11 = (double) j + 0.5D + (double) aint[0][0] * 0.5D;
                double d13 = (double) k + 0.5D + (double) aint[0][2] * 0.5D;
                double d14 = (double) j + 0.5D + (double) aint[1][0] * 0.5D;
                double d15 = (double) k + 0.5D + (double) aint[1][2] * 0.5D;

                d6 = d14 - d11;
                d7 = d15 - d13;
                double d16;
                double d17;

                if (d6 == 0.0D) {
                    this.locX = (double) j + 0.5D;
                    d12 = this.locZ - (double) k;
                } else if (d7 == 0.0D) {
                    this.locZ = (double) k + 0.5D;
                    d12 = this.locX - (double) j;
                } else {
                    d16 = this.locX - d11;
                    d17 = this.locZ - d13;
                    d12 = (d16 * d6 + d17 * d7) * 2.0D;
                }

                this.locX = d11 + d6 * d12;
                this.locZ = d13 + d7 * d12;
                this.setPosition(this.locX, this.locY + (double) this.height, this.locZ);
                d16 = this.motX;
                d17 = this.motZ;
                if (this.passenger != null) {
                    d16 *= 0.75D;
                    d17 *= 0.75D;
                }

                if (d16 < -d4) {
                    d16 = -d4;
                }

                if (d16 > d4) {
                    d16 = d4;
                }

                if (d17 < -d4) {
                    d17 = -d4;
                }

                if (d17 > d4) {
                    d17 = d4;
                }

                this.move(d16, 0.0D, d17);
                if (aint[0][1] != 0 && MathHelper.floor(this.locX) - j == aint[0][0] && MathHelper.floor(this.locZ) - k == aint[0][2]) {
                    this.setPosition(this.locX, this.locY + (double) aint[0][1], this.locZ);
                } else if (aint[1][1] != 0 && MathHelper.floor(this.locX) - j == aint[1][0] && MathHelper.floor(this.locZ) - k == aint[1][2]) {
                    this.setPosition(this.locX, this.locY + (double) aint[1][1], this.locZ);
                }

                // CraftBukkit
                if (this.passenger != null || !this.slowWhenEmpty) {
                    this.motX *= 0.996999979019165D;
                    this.motY *= 0.0D;
                    this.motZ *= 0.996999979019165D;
                } else {
                    if (this.type == 2) {
                        double d18 = this.b * this.b + this.c * this.c;

                        if (d18 > 1.0E-4D) {
                            d18 = (double) MathHelper.sqrt(d18);
                            this.b /= d18;
                            this.c /= d18;
                            double d19 = 0.04D;

                            this.motX *= 0.800000011920929D;
                            this.motY *= 0.0D;
                            this.motZ *= 0.800000011920929D;
                            this.motX += this.b * d19;
                            this.motZ += this.c * d19;
                        } else {
                            this.motX *= 0.8999999761581421D;
                            this.motY *= 0.0D;
                            this.motZ *= 0.8999999761581421D;
                        }
                    }

                    this.motX *= 0.9599999785423279D;
                    this.motY *= 0.0D;
                    this.motZ *= 0.9599999785423279D;
                }

                Vec3D vec3d1 = this.a(this.locX, this.locY, this.locZ);

                if (vec3d1 != null && vec3d != null) {
                    double d20 = (vec3d.d - vec3d1.d) * 0.05D;

                    d10 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
                    if (d10 > 0.0D) {
                        this.motX = this.motX / d10 * (d10 + d20);
                        this.motZ = this.motZ / d10 * (d10 + d20);
                    }

                    this.setPosition(this.locX, vec3d1.d, this.locZ);
                }

                int j1 = MathHelper.floor(this.locX);
                int k1 = MathHelper.floor(this.locZ);

                if (j1 != j || k1 != k) {
                    d10 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
                    this.motX = d10 * (double) (j1 - j);
                    this.motZ = d10 * (double) (k1 - k);
                }

                double d21;

                if (this.type == 2) {
                    d21 = this.b * this.b + this.c * this.c;
                    if (d21 > 1.0E-4D && this.motX * this.motX + this.motZ * this.motZ > 0.001D) {
                        d21 = (double) MathHelper.sqrt(d21);
                        this.b /= d21;
                        this.c /= d21;
                        if (this.b * this.motX + this.c * this.motZ < 0.0D) {
                            this.b = 0.0D;
                            this.c = 0.0D;
                        } else {
                            this.b = this.motX;
                            this.c = this.motZ;
                        }
                    }
                }

                if (flag) {
                    d21 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
                    if (d21 > 0.01D) {
                        double d22 = 0.06D;

                        this.motX += this.motX / d21 * d22;
                        this.motZ += this.motZ / d21 * d22;
                    } else if (i1 == 1) {
                        if (this.world.t(j - 1, i, k)) {
                            this.motX = 0.02D;
                        } else if (this.world.t(j + 1, i, k)) {
                            this.motX = -0.02D;
                        }
                    } else if (i1 == 0) {
                        if (this.world.t(j, i, k - 1)) {
                            this.motZ = 0.02D;
                        } else if (this.world.t(j, i, k + 1)) {
                            this.motZ = -0.02D;
                        }
                    }
                }
            } else {
                if (this.motX < -d4) {
                    this.motX = -d4;
                }

                if (this.motX > d4) {
                    this.motX = d4;
                }

                if (this.motZ < -d4) {
                    this.motZ = -d4;
                }

                if (this.motZ > d4) {
                    this.motZ = d4;
                }

                if (this.onGround) {
                    // CraftBukkit start
                    this.motX *= this.derailedX;
                    this.motY *= this.derailedY;
                    this.motZ *= this.derailedZ;
                    // CraftBukkit end
                }

                this.move(this.motX, this.motY, this.motZ);
                if (!this.onGround) {
                    // CraftBukkit start
                    this.motX *= this.flyingX;
                    this.motY *= this.flyingY;
                    this.motZ *= this.flyingZ;
                    // CraftBukkit end
                }
            }

            this.D();
            this.pitch = 0.0F;
            double d23 = this.lastX - this.locX;
            double d24 = this.lastZ - this.locZ;

            if (d23 * d23 + d24 * d24 > 0.001D) {
                this.yaw = (float) (Math.atan2(d24, d23) * 180.0D / 3.141592653589793D);
                if (this.f) {
                    this.yaw += 180.0F;
                }
            }

            double d25 = (double) MathHelper.g(this.yaw - this.lastYaw);

            if (d25 < -170.0D || d25 >= 170.0D) {
                this.yaw += 180.0F;
                this.f = !this.f;
            }

            this.b(this.yaw, this.pitch);

            // CraftBukkit start
            org.bukkit.World bworld = this.world.getWorld();
            Location from = new Location(bworld, prevX, prevY, prevZ, prevYaw, prevPitch);
            Location to = new Location(bworld, this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            Vehicle vehicle = (Vehicle) this.getBukkitEntity();

            this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleUpdateEvent(vehicle));

            if (!from.equals(to)) {
                this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleMoveEvent(vehicle, from, to));
            }
            // CraftBukkit end

            List list = this.world.getEntities(this, this.boundingBox.grow(0.20000000298023224D, 0.0D, 0.20000000298023224D));

            if (list != null && !list.isEmpty()) {
                for (int l1 = 0; l1 < list.size(); ++l1) {
                    Entity entity = (Entity) list.get(l1);

                    if (entity != this.passenger && entity.M() && entity instanceof EntityMinecart) {
                        entity.collide(this);
                    }
                }
            }

            if (this.passenger != null && this.passenger.dead) {
                if (this.passenger.vehicle == this) {
                    this.passenger.vehicle = null;
                }

                this.passenger = null;
            }

            if (this.e > 0) {
                --this.e;
            }

            if (this.e <= 0) {
                this.b = this.c = 0.0D;
            }

            this.e(this.e > 0);
        }
    }
	
}