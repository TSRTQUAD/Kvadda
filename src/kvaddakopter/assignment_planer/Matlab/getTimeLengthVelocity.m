%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%     Trajectory length and time calculations      %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Calculate the length of the trajectory and the total time. This is done
% by dividing each local lenght in the predifined velocity.

function [trajectorylength,totaltime,velocity] = ...
    getTimeLengthVelocity( trajectory )

% Set up predifiend velocities and preallocate vectors
localtrajectorylength = zeros(size(trajectory,1),1);
velocity = localtrajectorylength; totaltime = 0;
v1 = 0.5; v2 = 1; v3 = 2;               % [m/s]

for ii = 2:size(trajectory,1)
    localtrajectorylength(ii) = lldistkm(trajectory(ii-1,:),...
        trajectory(ii,:))*1e3;
    if localtrajectorylength(ii) <= 1.5
        velocity(ii) = v1;
        totaltime = totaltime + localtrajectorylength(ii)/v1;
    elseif (localtrajectorylength(ii) > 1.5)...
            && (localtrajectorylength(ii) <= 2.3)
        velocity(ii) = v2;
        totaltime = totaltime + localtrajectorylength(ii)/v2;
    else
        velocity(ii) = v3;
        totaltime = totaltime + localtrajectorylength(ii)/v3;
    end
end

% Calculate the total trajectory length
trajectorylength = sum(localtrajectorylength);

% Calculate the estimated time needed to fly along the trajectory
totaltime = totaltime/60;               % [min]

