function [besttrajectory,besttrajectoryfullsize] = ...
    getTrajectory( object, tmpcostmat, nodes, startpoint )
% The TSP-solver kan only handle integers, rescale the costmatrix to fit
% each element in an 32-bit unsigned variable.
normalizationfactor = 2^16/max([max(max(tmpcostmat.lon)),max(max...
    (tmpcostmat.lat)),max(max(tmpcostmat.cross))]);

pattern = {'lon' 'lat' 'cross'};
bestsolution = inf;
for ii = 1:3
    costmat = round(normalizationfactor*tmpcostmat.(pattern{ii}));
    trajectory.(pattern{ii}) = tspsolver(nodes,costmat);
    
    % ============ Interpolate trajectory ==============
    % Add the startpoint to the beginning and end of the trajectory icluding
    % some points inbetween to straighten up the future interpolation
    tmptrajectory = getStartEndPath(startpoint, trajectory.(pattern{ii}));
    
    % Interpolate using parametric splines, the first argument determines
    % the nr of nodes to interpolate between each nodpair in the trajectory
    rawtrajectory = interparc(1e3,tmptrajectory(:,1),...
        tmptrajectory(:,2),'spline');
    
    % ================= Last check =====================
    % Search for points in forbidden areas and put them on the edge
    rawtrajectory = lastCheck( rawtrajectory, object );
    tmptrajectory = lastCheck( tmptrajectory, object );

    % NOTE: If the fullsizetrajcetory are to be used, run the Douglas
    % Peucker algoritm on the rawtrajectory, the part that i commented.
    DPtrajectory = tmptrajectory; % DouglasPeucker(rawtrajectory);
    
    % This function calculates the total estimated time for the mission
    % which is used as a mesure of performance for the trajectory
    [~,totaltime] = getTimeLengthVelocity( DPtrajectory );
    
    currentsolution = totaltime;
    if currentsolution < bestsolution
        besttrajectory = DPtrajectory;
        besttrajectoryfullsize = rawtrajectory;
        bestsolution = currentsolution;
    end
end

end